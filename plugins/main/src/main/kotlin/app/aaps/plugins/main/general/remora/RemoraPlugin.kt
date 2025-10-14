package app.aaps.plugins.main.general.remora

import android.content.Context
import androidx.work.WorkManager
import app.aaps.core.data.configuration.Constants
import app.aaps.core.data.model.BS
import app.aaps.core.data.model.CA
import app.aaps.core.data.model.GlucoseUnit
import app.aaps.core.data.model.TT
import app.aaps.core.data.plugin.PluginType
import app.aaps.core.data.ue.Action
import app.aaps.core.data.ue.Sources
import app.aaps.core.data.ue.ValueWithUnit
import app.aaps.core.interfaces.aps.Loop
import app.aaps.core.interfaces.constraints.ConstraintsChecker
import app.aaps.core.interfaces.db.PersistenceLayer
import app.aaps.core.interfaces.iob.IobCobCalculator
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.overview.LastBgData
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
import app.aaps.core.interfaces.smsCommunicator.SmsCommunicator
import app.aaps.core.interfaces.utils.DateUtil
import app.aaps.core.interfaces.utils.HardLimits
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.objects.constraints.ConstraintObject
import app.aaps.plugins.main.R
import dagger.Lazy
import de.tebbeubben.remora.lib.LibraryMode
import de.tebbeubben.remora.lib.RemoraLib
import de.tebbeubben.remora.lib.commands.CommandHandler
import de.tebbeubben.remora.lib.commands.wrapError
import de.tebbeubben.remora.lib.commands.wrapSuccess
import de.tebbeubben.remora.lib.model.commands.RemoraCommand
import de.tebbeubben.remora.lib.model.commands.RemoraCommandData
import de.tebbeubben.remora.lib.model.commands.RemoraCommandError
import de.tebbeubben.remora.lib.model.commands.RemoraStatusSnapshot
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
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
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
    private val iobCobCalculator: IobCobCalculator,
    private val lastBgData: LastBgData,
    private val hardLimits: HardLimits,
    private val smsCommunicator: Lazy<SmsCommunicator>,
) : PluginBase(
    PluginDescription()
        .mainType(PluginType.GENERAL)
        .fragmentClass(RemoraFragment::class.java.name)
        .pluginIcon(app.aaps.core.objects.R.drawable.ic_remora)
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

    @OptIn(ExperimentalTime::class)
    override suspend fun validateStatusSnapshot(snapshot: RemoraStatusSnapshot): RemoraCommandError? {
        val currentCob = iobCobCalculator.getCobInfo("Remora").displayCob
        if (currentCob == null && !snapshot.cob.isNaN()) return RemoraCommandError.COB_MISMATCH
        if (currentCob != null && snapshot.cob.isNaN()) return RemoraCommandError.COB_MISMATCH

        if (
            currentCob != null &&
            !snapshot.cob.isNaN() &&
            abs(currentCob - snapshot.cob) >= 20
        ) {
            return RemoraCommandError.COB_MISMATCH
        }

        val bolusIob = iobCobCalculator.calculateIobFromBolus().iob.toFloat()
        val basalIob = iobCobCalculator.calculateIobFromTempBasalsIncludingConvertedExtended().basaliob
        val totalIob = bolusIob + basalIob

        if (abs(totalIob - snapshot.iob) >= hardLimits.maxBolus() / 10) return RemoraCommandError.IOB_MISMATCH

        val lastBg = lastBgData.lastBg()
        val bg = lastBg?.let { it.smoothed ?: it.value } ?: Double.NaN

        if (!bg.isNaN()) {
            if (snapshot.bg.isNaN()) return RemoraCommandError.BG_MISMATCH
            if (abs(bg - snapshot.bg) >= 20) return RemoraCommandError.BG_MISMATCH
        }
        if (!bg.isNaN() && abs(bg - snapshot.bg) >= 20) return RemoraCommandError.IOB_MISMATCH

        val lastBolus = persistenceLayer.getNewestBolusOfType(BS.Type.NORMAL)
        Duration.ZERO.absoluteValue
        if (
            lastBolus != null &&
            (snapshot.lastBolusTime - Instant.fromEpochMilliseconds(lastBolus.timestamp)).absoluteValue >= 30.seconds &&
            abs(snapshot.lastBolusAmount - lastBolus.amount) > 0.1
        ) {
            return RemoraCommandError.LAST_BOLUS_MISMATCH
        }

        return null
    }

    override suspend fun prepareTreatment(data: RemoraCommandData.Treatment): CommandHandler.Result<RemoraCommandData.Treatment> {
        val tt = data.temporaryTarget
        when {
            data.carbsAmount < 0f                           -> return wrapError(RemoraCommandError.INVALID_VALUE)
            data.bolusAmount < 0f                           -> return wrapError(RemoraCommandError.INVALID_VALUE)
            tt is RemoraCommandData.Treatment.TemporaryTarget.Set &&
                tt.duration <= Duration.ZERO                -> return wrapError(RemoraCommandError.INVALID_VALUE)

            data.bolusAmount > 0f && data.timestamp == null -> when {
                commandQueue.bolusInQueue()    -> return wrapError(RemoraCommandError.BOLUS_IN_PROGRESS)
                loop.runningMode.isSuspended() -> return wrapError(RemoraCommandError.PUMP_SUSPENDED)
            }
        }

        val constrainedBolusAmount = if (data.bolusAmount > 0f) {
            constraintsChecker.applyBolusConstraints(ConstraintObject(data.bolusAmount.toDouble(), aapsLogger)).value().toFloat()
        } else {
            0f
        }

        val constrainedCarbsAmount = if (data.carbsAmount > 0f) {
            constraintsChecker.applyCarbsConstraints(ConstraintObject(data.carbsAmount.roundToInt(), aapsLogger)).value().toFloat()
        } else {
            0f
        }

        var constrainedTempTarget = when (val tt = data.temporaryTarget) {
            RemoraCommandData.Treatment.TemporaryTarget.Cancel -> tt
            is RemoraCommandData.Treatment.TemporaryTarget.Set -> tt.copy(
                target = tt.target.coerceIn(Constants.MIN_TT_MGDL.toFloat(), Constants.MAX_TT_MGDL.toFloat()),
                duration = tt.duration.coerceAtMost(Constants.MAX_PROFILE_SWITCH_DURATION.minutes)
            )

            null                                               -> null
        }

        if (constrainedTempTarget is RemoraCommandData.Treatment.TemporaryTarget.Set && constrainedTempTarget.duration <= 0.minutes)
            constrainedTempTarget = null

        if (constrainedTempTarget == null && constrainedBolusAmount == 0f && constrainedCarbsAmount == 0f) {
            return wrapError(RemoraCommandError.INVALID_VALUE)
        }

        smsCommunicator.get().invalidateMessage()

        return wrapSuccess(
            data.copy(
                temporaryTarget = constrainedTempTarget,
                bolusAmount = constrainedBolusAmount,
                carbsAmount = constrainedCarbsAmount
            )
        )
    }

    override suspend fun CommandHandler.ExecutionScope.executeTreatment(data: RemoraCommandData.Treatment): CommandHandler.Result<RemoraCommandData.Treatment> = coroutineScope {
        val finalBolusAmount = if (data.bolusAmount > 0f && data.timestamp == null) {
            when (val result = deliverBolus(data.bolusAmount)) {
                is CommandHandler.Result.Error<*>       -> return@coroutineScope wrapError(result.error)
                is CommandHandler.Result.Success<Float> -> result.data
            }
        } else {
            data.bolusAmount
        }

        val timestamp = data.timestamp ?: Clock.System.now()

        val carbsJob = async {
            if (data.carbsAmount > 0f) {
                persistenceLayer.insertOrUpdateCarbs(
                    carbs = CA(
                        timestamp = (timestamp + data.carbsOffset).toEpochMilliseconds(),
                        amount = data.carbsAmount.toDouble(),
                        duration = data.carbsDuration.inWholeMilliseconds
                    ),
                    action = Action.CARBS,
                    source = Sources.Remora
                ).await()
            }
        }

        val bolusJob = async {
            if (data.bolusAmount > 0f && data.timestamp != null) {
                persistenceLayer.insertOrUpdateBolus(
                    bolus = BS(
                        timestamp = timestamp.toEpochMilliseconds(),
                        amount = data.bolusAmount.toDouble(),
                        type = BS.Type.NORMAL
                    ),
                    action = Action.CARBS,
                    source = Sources.Remora
                ).await()
            }
        }

        val ttJob = async {
            when (val tt = data.temporaryTarget) {
                RemoraCommandData.Treatment.TemporaryTarget.Cancel -> {
                    persistenceLayer.cancelCurrentTemporaryTargetIfAny(
                        timestamp = timestamp.toEpochMilliseconds(),
                        action = Action.TT,
                        source = Sources.Remora,
                        note = null,
                        listValues = listOf()
                    ).await()
                }

                is RemoraCommandData.Treatment.TemporaryTarget.Set -> {
                    val reason = when (tt.ttType) {
                        RemoraCommandData.Treatment.TemporaryTargetType.EATING_SOON -> TT.Reason.EATING_SOON
                        RemoraCommandData.Treatment.TemporaryTargetType.ACTIVITY    -> TT.Reason.ACTIVITY
                        RemoraCommandData.Treatment.TemporaryTargetType.HYPO        -> TT.Reason.HYPOGLYCEMIA
                        RemoraCommandData.Treatment.TemporaryTargetType.CUSTOM      -> TT.Reason.CUSTOM
                    }
                    persistenceLayer.insertAndCancelCurrentTemporaryTarget(
                        TT(
                            timestamp = timestamp.toEpochMilliseconds(),
                            duration = tt.duration.inWholeMilliseconds,
                            reason = reason,
                            lowTarget = tt.target.toDouble(),
                            highTarget = tt.target.toDouble()
                        ),
                        action = Action.TT,
                        source = Sources.Remora,
                        note = null,
                        listValues = listOf(
                            ValueWithUnit.Timestamp(timestamp.toEpochMilliseconds()),
                            ValueWithUnit.TETTReason(reason),
                            ValueWithUnit.fromGlucoseUnit(tt.target.toDouble(), GlucoseUnit.MGDL),
                            ValueWithUnit.Minute(tt.duration.inWholeMinutes.toInt())
                        )
                    ).await()
                }

                null                                               -> Unit
            }
        }

        carbsJob.await()
        bolusJob.await()
        ttJob.await()

        wrapSuccess(data.copy(bolusAmount = finalBolusAmount))
    }

    fun invalidateCommand() {
        scope.launch { remoraLib.invalidateCurrentCommand() }
    }

    private suspend fun CommandHandler.ExecutionScope.deliverBolus(amount: Float): CommandHandler.Result<Float> = coroutineScope {
        if (amount <= 0f) return@coroutineScope wrapError(RemoraCommandError.INVALID_VALUE)
        if (commandQueue.bolusInQueue()) return@coroutineScope wrapError(RemoraCommandError.BOLUS_IN_PROGRESS)
        if (loop.runningMode.isSuspended()) return@coroutineScope wrapError(RemoraCommandError.PUMP_SUSPENDED)
        val detailedBolusInfo = DetailedBolusInfo()
        detailedBolusInfo.insulin = amount.toDouble()
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
        wrapSuccess(result.bolusDelivered.toFloat())
    }
}