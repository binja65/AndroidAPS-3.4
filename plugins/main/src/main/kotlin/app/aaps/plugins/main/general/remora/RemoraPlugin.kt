package app.aaps.plugins.main.general.remora

import android.content.Context
import androidx.work.WorkManager
import app.aaps.core.data.model.TT
import app.aaps.core.data.plugin.PluginType
import app.aaps.core.data.ue.Action
import app.aaps.core.data.ue.Sources
import app.aaps.core.data.ue.ValueWithUnit
import app.aaps.core.interfaces.aps.Loop
import app.aaps.core.interfaces.constraints.ConstraintsChecker
import app.aaps.core.interfaces.db.PersistenceLayer
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.interfaces.plugin.PluginBase
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.interfaces.pump.DetailedBolusInfo
import app.aaps.core.interfaces.queue.Callback
import app.aaps.core.interfaces.queue.CommandQueue
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.rx.AapsSchedulers
import app.aaps.core.interfaces.rx.bus.RxBus
import app.aaps.core.interfaces.rx.events.Event
import app.aaps.core.interfaces.rx.events.EventDeviceStatusChange
import app.aaps.core.interfaces.rx.events.EventNewHistoryData
import app.aaps.core.interfaces.rx.events.EventOverviewBolusProgress
import app.aaps.core.interfaces.rx.events.EventProfileSwitchChanged
import app.aaps.core.interfaces.rx.events.EventPumpStatusChanged
import app.aaps.core.interfaces.rx.events.EventRunningModeChange
import app.aaps.core.interfaces.rx.events.EventTempTargetChange
import app.aaps.core.interfaces.rx.events.EventTherapyEventChange
import app.aaps.core.interfaces.rx.events.EventUpdateOverviewGraph
import app.aaps.core.interfaces.utils.DateUtil
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.UnitDoubleKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.objects.constraints.ConstraintObject
import app.aaps.plugins.main.R
import de.tebbeubben.remora.lib.LibraryMode
import de.tebbeubben.remora.lib.RemoraLib
import de.tebbeubben.remora.lib.commands.CommandHandler
import de.tebbeubben.remora.lib.commands.wrapError
import de.tebbeubben.remora.lib.commands.wrapSuccess
import de.tebbeubben.remora.lib.model.commands.RemoraCommand
import de.tebbeubben.remora.lib.model.commands.RemoraCommandData
import de.tebbeubben.remora.lib.model.commands.RemoraCommandError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@Singleton
class RemoraPlugin @Inject constructor(
    aapsLogger: AAPSLogger,
    rh: ResourceHelper,
    private val statusDataBuilder: StatusDataBuilder,
    val activePlugin: ActivePlugin,
    val aapsSchedulers: AapsSchedulers,
    private val commandQueue: CommandQueue,
    private val loop: Loop,
    private val constraintsChecker: ConstraintsChecker,
    private val preferences: Preferences,
    private val persistenceLayer: PersistenceLayer,
    private val profileUtil: ProfileUtil,
    private val dateUtil: DateUtil,
    context: Context,
    private val rxBus: RxBus,
    private val workManager: WorkManager,
) : PluginBase(
    PluginDescription()
        .mainType(PluginType.GENERAL)
        .fragmentClass(RemoraFragment::class.java.name)
        .pluginIcon(de.tebbeubben.remora.lib.R.drawable.remora_logo)
        .pluginName(R.string.remora)
        .shortName(R.string.remora_shortname)
        .description(R.string.description_remora),
    aapsLogger, rh
), CommandHandler {

    private val remoraLib = RemoraLib.initialize(context, LibraryMode.MAIN)

    init {
        remoraLib.setCommandHandler(this)
    }

    private val bolusProgressFlow get() = rxBus.toObservable(EventOverviewBolusProgress::class.java).asFlow()
    private val pumpStatusProgressFlow get() = rxBus.toObservable(EventPumpStatusChanged::class.java).asFlow()
    private val commandQueueInfoFlow get() = workManager.getWorkInfosByTagFlow("CommandQueue")
    private val calculationWorkflowInfoFlow get() = workManager.getWorkInfosByTagFlow("calculation")

    // These events will trigger re-sending the current status information to all followers
    private val mergedEvents: Flow<Event>
        get() = merge(
            rxBus.toObservable(EventNewHistoryData::class.java).asFlow(),
            rxBus.toObservable(EventTempTargetChange::class.java).asFlow(),
            rxBus.toObservable(EventTherapyEventChange::class.java).asFlow(),
            rxBus.toObservable(EventProfileSwitchChanged::class.java).asFlow(),
            rxBus.toObservable(EventRunningModeChange::class.java).asFlow(),
            rxBus.toObservable(EventDeviceStatusChange::class.java).asFlow(),
            rxBus.toObservable(EventUpdateOverviewGraph::class.java).asFlow()
        )

    // This flow will be triggered by any of the events from `mergedEvents`,
    // but waits for the command queue and calculation workflow to finish before triggering the update.
    private val updateFlow: Flow<Unit> = combine(
        mergedEvents,
        commandQueueInfoFlow,
        calculationWorkflowInfoFlow
    ) { _, commandQueueInfo, calcWorkflowInfo ->
        commandQueueInfo.any { !it.state.isFinished } || calcWorkflowInfo.any { !it.state.isFinished }
    }
        .filter { !it }
        .map { Unit }

    private var scope = CoroutineScope(Dispatchers.Default)

    @OptIn(FlowPreview::class)
    override fun onStart() {
        commandQueue.performing()
        scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            remoraLib.startup()
            updateFlow
                .debounce(1000)
                .collectLatest {
                    val statusData = statusDataBuilder.constructStatusData()
                    if (statusData != null) {
                        remoraLib.shareStatus(statusData)
                    }
                }
        }
    }

    override fun onStop() {
        scope.cancel()
        runBlocking {
            remoraLib.shutdown()
        }
    }

    override suspend fun prepareBolus(bolusData: RemoraCommandData.Bolus): CommandHandler.Result<RemoraCommandData.Bolus> = when {
        commandQueue.bolusInQueue()    -> wrapError(RemoraCommandError.BOLUS_IN_PROGRESS)
        loop.runningMode.isSuspended() -> wrapError(RemoraCommandError.PUMP_SUSPENDED)
        bolusData.bolusAmount == 0f    -> wrapError(RemoraCommandError.INVALID_VALUE)

        else                           -> {
            val constrained = constraintsChecker.applyBolusConstraints(ConstraintObject(bolusData.bolusAmount.toDouble(), aapsLogger)).value()
            wrapSuccess(bolusData.copy(bolusAmount = constrained.toFloat()))
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun CommandHandler.ExecutionScope.executeBolus(bolusData: RemoraCommandData.Bolus): CommandHandler.Result<RemoraCommandData.Bolus> =
        coroutineScope {
            if (bolusData.bolusAmount == 0f) return@coroutineScope wrapError(RemoraCommandError.INVALID_VALUE)
            if (commandQueue.bolusInQueue()) return@coroutineScope wrapError(RemoraCommandError.BOLUS_IN_PROGRESS)
            if (loop.runningMode.isSuspended()) return@coroutineScope wrapError(RemoraCommandError.PUMP_SUSPENDED)
            val detailedBolusInfo = DetailedBolusInfo()
            detailedBolusInfo.insulin = bolusData.bolusAmount.toDouble()
            val resultJob = async {
                suspendCoroutine {
                    val callback = object : Callback() {
                        override fun run() {
                            it.resume(result)
                        }
                    }
                    commandQueue.bolus(detailedBolusInfo, callback)
                }
            }
            var reportConnectionProgress = true
            if (activePlugin.activePump.isConnected()) {
                reportIntermediateProgress(RemoraCommand.Progress.Enqueued)
            }
            val progressJob = launch {
                merge(pumpStatusProgressFlow, bolusProgressFlow)
                    .collect { event ->
                        when (event) {
                            is EventPumpStatusChanged     -> when (event.status) {
                                EventPumpStatusChanged.Status.CONNECTING,
                                EventPumpStatusChanged.Status.HANDSHAKING,
                                     -> {
                                    if (reportConnectionProgress)
                                        reportIntermediateProgress(RemoraCommand.Progress.Connecting(event.secondsElapsed))
                                }

                                EventPumpStatusChanged.Status.CONNECTED,
                                EventPumpStatusChanged.Status.PERFORMING,
                                     -> {
                                    if (reportConnectionProgress)
                                        reportIntermediateProgress(RemoraCommand.Progress.Enqueued)
                                }

                                else -> Unit
                            }

                            is EventOverviewBolusProgress -> {
                                reportIntermediateProgress(RemoraCommand.Progress.Percentage(event.percent))
                                reportConnectionProgress = false
                            }
                        }
                    }
            }
            val result = resultJob.await()
            progressJob.cancel()
            if (!result.success) {
                if (!activePlugin.activePump.isConnected()) {
                    return@coroutineScope wrapError(RemoraCommandError.PUMP_TIMEOUT)
                }
                return@coroutineScope wrapError(RemoraCommandError.UNKNOWN)
            }
            if (bolusData.startEatingSoonTT) {
                val eatingSoonTTDuration = preferences.get(IntKey.OverviewEatingSoonDuration)
                val eatingSoonTT = preferences.get(UnitDoubleKey.OverviewEatingSoonTarget)
                persistenceLayer.insertAndCancelCurrentTemporaryTarget(
                    temporaryTarget = TT(
                        timestamp = dateUtil.now(),
                        duration = TimeUnit.MINUTES.toMillis(eatingSoonTTDuration.toLong()),
                        reason = TT.Reason.EATING_SOON,
                        lowTarget = profileUtil.convertToMgdl(eatingSoonTT, profileUtil.units),
                        highTarget = profileUtil.convertToMgdl(eatingSoonTT, profileUtil.units)
                    ),
                    action = Action.TT,
                    source = Sources.SMS,
                    note = null,
                    listValues = listOf(
                        ValueWithUnit.TETTReason(TT.Reason.EATING_SOON),
                        ValueWithUnit.Mgdl(profileUtil.convertToMgdl(eatingSoonTT, profileUtil.units)),
                        ValueWithUnit.Minute(TimeUnit.MILLISECONDS.toMinutes(TimeUnit.MINUTES.toMillis(eatingSoonTTDuration.toLong())).toInt())
                    )
                ).await()
            }
            wrapSuccess(bolusData.copy(bolusAmount = result.bolusDelivered.toFloat()))
        }
}