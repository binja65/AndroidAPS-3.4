package app.aaps.plugins.main.general.remora

import android.content.Context
import app.aaps.core.data.model.TT
import app.aaps.core.data.plugin.PluginType
import app.aaps.core.data.ue.Action
import app.aaps.core.data.ue.Sources
import app.aaps.core.data.ue.ValueWithUnit
import app.aaps.core.interfaces.aps.Loop
import app.aaps.core.interfaces.constraints.ConstraintsChecker
import app.aaps.core.interfaces.db.PersistenceLayer
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.logging.LTag
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.interfaces.plugin.PluginBase
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.profile.ProfileFunction
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.interfaces.pump.DetailedBolusInfo
import app.aaps.core.interfaces.queue.Callback
import app.aaps.core.interfaces.queue.CommandQueue
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.rx.AapsSchedulers
import app.aaps.core.interfaces.rx.bus.RxBus
import app.aaps.core.interfaces.rx.events.EventOverviewBolusProgress
import app.aaps.core.interfaces.rx.events.EventUpdateOverviewGraph
import app.aaps.core.interfaces.utils.DateUtil
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.UnitDoubleKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.objects.constraints.ConstraintObject
import app.aaps.plugins.main.R
import de.tebbeubben.remora.lib.commands.CommandHandler
import de.tebbeubben.remora.lib.LibraryMode
import de.tebbeubben.remora.lib.RemoraLib
import de.tebbeubben.remora.lib.commands.wrapError
import de.tebbeubben.remora.lib.commands.wrapSuccess
import de.tebbeubben.remora.lib.model.commands.RemoraCommandData
import de.tebbeubben.remora.lib.model.commands.RemoraCommandError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    private var scope = CoroutineScope(Dispatchers.Default)

    init {
        remoraLib.setCommandHandler(this)
    }

    override fun onStart() {
        scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            remoraLib.startup()
            withContext(Dispatchers.IO) {
                activePlugin.activeOverview.overviewBus
                    .toObservable(EventUpdateOverviewGraph::class.java)
                    .debounce(1L, TimeUnit.SECONDS)
                    .asFlow()
                    .collectLatest {
                        val statusData = statusDataBuilder.constructStatusData()
                        if (statusData != null) {
                            RemoraLib.instance.shareStatus(statusData)
                        }
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
            reportIntermediateProgress(null)
            val progressJob = launch {
                val progressFlow = rxBus.toObservable(EventOverviewBolusProgress::class.java).asFlow().map { it.percent }
                progressFlow.collectLatest(::reportIntermediateProgress)
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