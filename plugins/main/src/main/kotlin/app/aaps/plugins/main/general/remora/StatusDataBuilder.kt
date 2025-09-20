package app.aaps.plugins.main.general.remora

import app.aaps.core.data.model.BS
import app.aaps.core.data.model.GlucoseUnit
import app.aaps.core.data.model.RM
import app.aaps.core.data.model.SourceSensor
import app.aaps.core.data.model.TB
import app.aaps.core.data.model.TE
import app.aaps.core.data.model.TT
import app.aaps.core.data.model.TrendArrow
import app.aaps.core.data.time.T
import app.aaps.core.interfaces.aps.IobTotal
import app.aaps.core.interfaces.aps.Loop
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.db.PersistenceLayer
import app.aaps.core.interfaces.iob.GlucoseStatusProvider
import app.aaps.core.interfaces.iob.IobCobCalculator
import app.aaps.core.interfaces.overview.LastBgData
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.interfaces.profile.ProfileFunction
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.interfaces.receivers.ReceiverStatusStore
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.rx.bus.RxBus
import app.aaps.core.interfaces.utils.HardLimits
import app.aaps.core.interfaces.utils.TrendCalculator
import app.aaps.core.keys.UnitDoubleKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.objects.extensions.combine
import app.aaps.core.objects.extensions.target
import app.aaps.core.objects.profile.ProfileSealed
import de.tebbeubben.remora.lib.model.status.RemoraStatusData
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.BasalDataPoint
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.BgReading
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.Bolus
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.BucketedDataPoint
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.CarbEntry
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.Deltas
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.DisplayBg
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.ExtendedBolus
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.Prediction
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.ProfileSwitch
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.TargetDataPoint
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.TemporaryBasal
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.TemporaryTarget
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.TemporaryTargetReason
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.TherapyEvent
import de.tebbeubben.remora.lib.model.status.RemoraStatusData.TherapyEventType
import org.joda.time.DateTimeZone
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class StatusDataBuilder @Inject constructor(
    private val persistenceLayer: PersistenceLayer,
    private val iobCobCalculator: IobCobCalculator,
    private val activePlugin: ActivePlugin,
    private val loop: Loop,
    private val config: Config,
    private val profileFunction: ProfileFunction,
    private val rxBus: RxBus,
    private val hardLimits: HardLimits,
    private val rh: ResourceHelper,
    private val lastBgData: LastBgData,
    private val glucoseStatusProvider: GlucoseStatusProvider,
    private val preferences: Preferences,
    private val trendCalculator: TrendCalculator,
    private val receiverStatusStore: ReceiverStatusStore,
    private val profileUtil: ProfileUtil
) {

    fun constructStatusData(): RemoraStatusData? {
        val nowInstant = Clock.System.now()
        val nowMillis = nowInstant.toEpochMilliseconds()
        val endMillis = (nowInstant + 6.hours).toEpochMilliseconds()
        val startMillis = (nowInstant - 24.hours - 2.minutes).toEpochMilliseconds()

        val profileSwitch = persistenceLayer.getEffectiveProfileSwitchActiveAt(nowMillis) ?: return null
        val profile = profileFunction.getProfile(nowMillis) ?: return null

        val basalData = iobCobCalculator.getBasalData(profile, nowMillis)

        val tempTarget = persistenceLayer.getTemporaryTargetActiveAt(nowMillis)

        val shortStatus = RemoraStatusData.Short(
            timestamp = nowInstant,
            timezone = DateTimeZone.getDefault().id,
            displayCob = iobCobCalculator.getCobInfo("Remora").displayCob?.toFloat(),
            futureCarbs = iobCobCalculator.getCobInfo("Remora").futureCarbs.toFloat(),
            activeProfile = profileSwitch.originalProfileName,
            activeProfilePercentage = profileSwitch.originalPercentage,
            activeProfileShift = profileSwitch.originalTimeshift.milliseconds.inWholeHours.toInt(),
            activeProfileStart = Instant.fromEpochMilliseconds(profileSwitch.timestamp),
            activeProfileDuration = if (profileSwitch.originalDuration != 0L) profileSwitch.originalDuration.milliseconds else null,
            usesMgdl = profileFunction.getUnits() == GlucoseUnit.MGDL,
            lowBgThreshold = profileUtil.convertToMgdl(preferences.get(UnitDoubleKey.OverviewLowMark), profileUtil.units).toFloat(),
            highBgThreshold = profileUtil.convertToMgdl(preferences.get(UnitDoubleKey.OverviewHighMark), profileUtil.units).toFloat(),
            displayBg = lastBgData.lastBg()?.let { bg ->
                DisplayBg(
                    timestamp = Instant.fromEpochMilliseconds(bg.timestamp),
                    value = bg.value.toFloat(),
                    smoothedValue = bg.smoothed?.toFloat(),
                    trendArrow = convertAapsTrendToRemoraTrend(trendCalculator.getTrendArrow(iobCobCalculator.ads) ?: TrendArrow.NONE),
                    deltas = glucoseStatusProvider.glucoseStatusData?.let { status ->
                        Deltas(
                            delta = status.delta.toFloat(),
                            shortAverageDelta = status.shortAvgDelta.toFloat(),
                            longAverageDelta = status.longAvgDelta.toFloat()
                        )
                    }
                )
            },
            bolusIob = iobCobCalculator.calculateIobFromBolus().iob.toFloat(),
            basalIob = iobCobCalculator.calculateIobFromTempBasalsIncludingConvertedExtended().basaliob.toFloat(),
            reservoirLevel = activePlugin.activePump.reservoirLevel.let { if (it >= 0) it.toFloat() else null }, // Assuming -1 or similar for unknown
            isReservoirLevelMax = activePlugin.activePump.let { pump ->
                pump.reservoirLevel >= pump.pumpDescription.maxResorvoirReading && pump.pumpDescription.isPatchPump
            },
            sensorChangedAt = persistenceLayer.getLastTherapyRecordUpToNow(TE.Type.SENSOR_CHANGE)?.timestamp?.let { Instant.fromEpochMilliseconds(it) },
            sensorBatteryLevel = activePlugin.activeBgSource.sensorBatteryLevel.let { if (it >= 0) it else null },
            batteryChangedAt = persistenceLayer.getLastTherapyRecordUpToNow(TE.Type.PUMP_BATTERY_CHANGE)?.timestamp?.let { Instant.fromEpochMilliseconds(it) },
            batteryLevel = activePlugin.activePump.batteryLevel.let { if (it >= 0) it else null },
            cannulaChangedAt = persistenceLayer.getLastTherapyRecordUpToNow(TE.Type.CANNULA_CHANGE)?.timestamp?.let { Instant.fromEpochMilliseconds(it) },
            podChangedAt = if (activePlugin.activePump.pumpDescription.isPatchPump) {
                persistenceLayer.getLastTherapyRecordUpToNow(TE.Type.CANNULA_CHANGE)?.timestamp?.let { Instant.fromEpochMilliseconds(it) }
            } else null,
            insulinChangedAt = persistenceLayer.getLastTherapyRecordUpToNow(TE.Type.INSULIN_CHANGE)?.timestamp?.let { Instant.fromEpochMilliseconds(it) },
            runningMode = convertAapsRmModeToRemoraMode(loop.runningMode),
            runningModeStart = Instant.fromEpochMilliseconds(loop.runningModeRecord.timestamp),
            runningModeDuration = if (loop.runningModeRecord.duration == 0L) null else loop.runningModeRecord.duration.milliseconds,
            baseBasal = basalData.basal.toFloat(),
            tempBasalAbsolute = if (basalData.isTempBasalRunning) basalData.tempBasalAbsolute.toFloat() else null,
            target = tempTarget?.target()?.toFloat() ?: profile.getTargetMgdl().toFloat(),
            tempTargetStart = tempTarget?.timestamp?.let { Instant.fromEpochMilliseconds(it) },
            tempTargetDuration = tempTarget?.duration?.milliseconds,
            autosensRatio = iobCobCalculator.ads.getAutosensDataAtTime(nowMillis)?.autosensResult?.ratio?.toFloat() ?: 1f,
            deviceBattery = receiverStatusStore.batteryLevel,
            isCharging = receiverStatusStore.isCharging
        )

        return RemoraStatusData(
            short = shortStatus,
            isFakingTemps = activePlugin.activePump.isFakingTempsByExtendedBoluses,
            bucketedData = getBucketedData(nowMillis, startMillis, endMillis),
            basalData = getBasalData(startMillis, endMillis),
            targetData = getTargetData(startMillis, endMillis),
            predictions = getPredictions(),
            bgReadings = getBgReadings(),
            boluses = getBoluses(startMillis, endMillis),
            carbs = getCarbs(startMillis, endMillis),
            profileSwitches = getProfileSwitches(startMillis, endMillis),
            therapyEvents = getTherapyEvents(startMillis, endMillis),
            temporaryBasals = getTemporaryBasals(startMillis, endMillis),
            temporaryTargets = getTemporaryTargets(startMillis, endMillis),
            extendedBoluses = getExtendedBoluses(startMillis, endMillis),
            profiles = getProfiles(),
            runningModes = getRunningModeData(startMillis, endMillis)
        )
    }

    private fun convertAapsRmModeToRemoraMode(mode: RM.Mode): RemoraStatusData.RunningMode = when (mode) {
        RM.Mode.OPEN_LOOP         -> RemoraStatusData.RunningMode.OPEN_LOOP
        RM.Mode.CLOSED_LOOP       -> RemoraStatusData.RunningMode.CLOSED_LOOP
        RM.Mode.CLOSED_LOOP_LGS   -> RemoraStatusData.RunningMode.CLOSED_LOOP_LGS
        RM.Mode.DISABLED_LOOP     -> RemoraStatusData.RunningMode.DISABLED_LOOP
        RM.Mode.SUPER_BOLUS       -> RemoraStatusData.RunningMode.SUPER_BOLUS
        RM.Mode.DISCONNECTED_PUMP -> RemoraStatusData.RunningMode.DISCONNECTED_PUMP
        RM.Mode.SUSPENDED_BY_PUMP -> RemoraStatusData.RunningMode.SUSPENDED_BY_PUMP
        RM.Mode.SUSPENDED_BY_USER -> RemoraStatusData.RunningMode.SUSPENDED_BY_USER
        RM.Mode.RESUME            -> error("Invalid mode")
    }

    private fun getRunningModeData(start: Long, end: Long): List<RemoraStatusData.RunningModeDataPoint> =
        generateSequence(start) { it + T.mins(5).msecs() }
            .takeWhile { it <= end }
            .map { time ->
                val mode = persistenceLayer.getRunningModeActiveAt(time)
                RemoraStatusData.RunningModeDataPoint(
                    timestamp = Instant.fromEpochMilliseconds(time),
                    runningMode = when (mode.mode) {
                        RM.Mode.OPEN_LOOP         -> RemoraStatusData.RunningMode.OPEN_LOOP
                        RM.Mode.CLOSED_LOOP       -> RemoraStatusData.RunningMode.CLOSED_LOOP
                        RM.Mode.CLOSED_LOOP_LGS   -> RemoraStatusData.RunningMode.CLOSED_LOOP_LGS
                        RM.Mode.DISABLED_LOOP     -> RemoraStatusData.RunningMode.DISABLED_LOOP
                        RM.Mode.SUPER_BOLUS       -> RemoraStatusData.RunningMode.SUPER_BOLUS
                        RM.Mode.DISCONNECTED_PUMP -> RemoraStatusData.RunningMode.DISCONNECTED_PUMP
                        RM.Mode.SUSPENDED_BY_PUMP -> RemoraStatusData.RunningMode.SUSPENDED_BY_PUMP
                        RM.Mode.SUSPENDED_BY_USER -> RemoraStatusData.RunningMode.SUSPENDED_BY_USER
                        RM.Mode.RESUME            -> error("Invalid mode")
                    }
                )
            }
            .fold(mutableListOf()) { acc, dp ->
                if (acc.isEmpty() || acc.last().runningMode != dp.runningMode) acc += dp
                acc
            }

    private fun convertAapsTrendToRemoraTrend(aapsTrend: TrendArrow): RemoraStatusData.TrendArrow {
        return when (aapsTrend) {
            TrendArrow.NONE            -> RemoraStatusData.TrendArrow.NONE
            TrendArrow.TRIPLE_UP       -> RemoraStatusData.TrendArrow.TRIPLE_UP
            TrendArrow.DOUBLE_UP       -> RemoraStatusData.TrendArrow.DOUBLE_UP
            TrendArrow.SINGLE_UP       -> RemoraStatusData.TrendArrow.SINGLE_UP
            TrendArrow.FORTY_FIVE_UP   -> RemoraStatusData.TrendArrow.FORTY_FIVE_UP
            TrendArrow.FLAT            -> RemoraStatusData.TrendArrow.FLAT
            TrendArrow.FORTY_FIVE_DOWN -> RemoraStatusData.TrendArrow.FORTY_FIVE_DOWN
            TrendArrow.SINGLE_DOWN     -> RemoraStatusData.TrendArrow.SINGLE_DOWN
            TrendArrow.DOUBLE_DOWN     -> RemoraStatusData.TrendArrow.DOUBLE_DOWN
            TrendArrow.TRIPLE_DOWN     -> RemoraStatusData.TrendArrow.TRIPLE_DOWN
        }
    }

    private fun getProfiles(): List<String> {
        val profileStore = activePlugin.activeProfileSource.profile
        return profileStore?.getProfileList()?.filter { profile ->
            val profileToCheck = activePlugin.activeProfileSource.profile?.getSpecificProfile(profile.toString())
            profileToCheck != null &&
                ProfileSealed.Pure(profileToCheck, activePlugin)
                    .isValid("ProfileSwitch", activePlugin.activePump, config, rh, rxBus, hardLimits, false)
                    .isValid
        }?.map { it.toString() } ?: emptyList()
    }

    private fun TT.toModel() = TemporaryTarget(
        timestamp = Instant.fromEpochMilliseconds(timestamp),
        id = id,
        duration = duration.milliseconds,
        target = target().toFloat(),
        reason = when (reason) {
            TT.Reason.CUSTOM       -> TemporaryTargetReason.CUSTOM
            TT.Reason.HYPOGLYCEMIA -> TemporaryTargetReason.HYPOGLYCEMIA
            TT.Reason.ACTIVITY     -> TemporaryTargetReason.ACTIVITY
            TT.Reason.EATING_SOON  -> TemporaryTargetReason.EATING_SOON
            TT.Reason.AUTOMATION   -> TemporaryTargetReason.AUTOMATION
            TT.Reason.WEAR         -> TemporaryTargetReason.WEAR
        }
    )

    private fun getTemporaryTargets(start: Long, end: Long): List<TemporaryTarget> =
        persistenceLayer.getTemporaryTargetDataFromTimeToTime(start, end, true).blockingGet().map { it.toModel() }

    private fun TB.toModel() = TemporaryBasal(
        timestamp = Instant.fromEpochMilliseconds(timestamp),
        id = id,
        isAbsolute = isAbsolute,
        rate = rate.toFloat(),
        duration = duration.milliseconds
    )

    private fun getTemporaryBasals(start: Long, end: Long): List<TemporaryBasal> =
        persistenceLayer.getTemporaryBasalsActiveBetweenTimeAndTime(start, end).map { it.toModel() }

    private fun getTherapyEvents(start: Long, end: Long): List<TherapyEvent> =
        persistenceLayer.getTherapyEventDataFromToTime(start, end).blockingGet().map { te ->
            TherapyEvent(
                timestamp = Instant.fromEpochMilliseconds(te.timestamp),
                id = te.id,
                duration = te.duration.milliseconds,
                type = when (te.type) {
                    TE.Type.NONE                  -> TherapyEventType.NONE
                    TE.Type.CANNULA_CHANGE        -> TherapyEventType.CANNULA_CHANGE
                    TE.Type.INSULIN_CHANGE        -> TherapyEventType.INSULIN_CHANGE
                    TE.Type.PUMP_BATTERY_CHANGE   -> TherapyEventType.PUMP_BATTERY_CHANGE
                    TE.Type.SENSOR_CHANGE         -> TherapyEventType.SENSOR_CHANGE
                    TE.Type.SENSOR_STARTED        -> TherapyEventType.SENSOR_STARTED
                    TE.Type.SENSOR_STOPPED        -> TherapyEventType.SENSOR_STOPPED
                    TE.Type.FINGER_STICK_BG_VALUE -> TherapyEventType.FINGER_STICK_BG_VALUE
                    TE.Type.EXERCISE              -> TherapyEventType.EXERCISE
                    TE.Type.ANNOUNCEMENT          -> TherapyEventType.ANNOUNCEMENT
                    TE.Type.QUESTION              -> TherapyEventType.QUESTION
                    TE.Type.NOTE                  -> TherapyEventType.NOTE
                    TE.Type.APS_OFFLINE           -> TherapyEventType.APS_OFFLINE
                    else                          -> TherapyEventType.NONE
                },
                note = te.note,
                glucose = te.glucose?.toFloat(),
                glucoseType = when (te.glucoseType) {
                    TE.MeterType.FINGER -> RemoraStatusData.MeterType.FINGER
                    TE.MeterType.SENSOR -> RemoraStatusData.MeterType.SENSOR
                    TE.MeterType.MANUAL -> RemoraStatusData.MeterType.MANUAL
                    else                -> null
                },
                isMgdl = profileFunction.getUnits() == GlucoseUnit.MGDL
            )
        }

    private fun getBucketedData(now: Long, start: Long, end: Long): List<BucketedDataPoint> {
        val existingBucketedData = iobCobCalculator.ads.bucketedData ?: emptyList()

        val existingBuckets = existingBucketedData.map { bucket ->
            BucketedDataPoint(
                timestamp = Instant.fromEpochMilliseconds(bucket.timestamp),
                bgData = RemoraStatusData.BgData(
                    value = (bucket.smoothed ?: bucket.value).toFloat(),
                    filledGap = bucket.filledGap
                ),
                insulinData = getInsulinDataFor(bucket.timestamp),
                autosensData = getAutosensDataFor(bucket.timestamp)
            )
        }
        val firstFutureBucketTime = existingBucketedData.maxByOrNull { it.timestamp }?.timestamp?.plus(T.mins(5).msecs()) ?: now
        val missingFutureBucketTimestamps = generateSequence(firstFutureBucketTime) { it + T.mins(5).msecs() }.takeWhile { it <= end }.toList()

        val lastPastBucketTime = existingBucketedData.minByOrNull { it.timestamp }?.timestamp?.minus(T.mins(5).msecs()) ?: (now - T.mins(5).msecs())
        val missingPastBucketTimestamps = generateSequence(lastPastBucketTime) { it - T.mins(5).msecs() }.takeWhile { it >= start }.toList()

        val missingBuckets = (missingFutureBucketTimestamps + missingPastBucketTimestamps)
            .map { time ->
                BucketedDataPoint(
                    timestamp = Instant.fromEpochMilliseconds(time),
                    bgData = null,
                    insulinData = getInsulinDataFor(time),
                    autosensData = null
                )
            }

        return (existingBuckets + missingBuckets)
    }

    private fun getAutosensDataFor(time: Long) =
        iobCobCalculator.ads.getAutosensDataAtTime(time)?.let {
            RemoraStatusData.AutosensData(
                ratio = it.autosensResult.ratio.toFloat(),
                cob = it.cob.toFloat(),
                carbsFromBolus = it.carbsFromBolus.toFloat(),
                bgi = it.bgi.toFloat(),
                deviation = it.deviation.toFloat(),
                type = when (it.type) {
                    "", "non-meal" -> when (it.pastSensitivity) {
                        "C" -> RemoraStatusData.AutosensType.CSF
                        "+" -> RemoraStatusData.AutosensType.POSITIVE
                        "-" -> RemoraStatusData.AutosensType.NEGATIVE
                        else -> RemoraStatusData.AutosensType.NEUTRAL
                    }
                    "uam" -> RemoraStatusData.AutosensType.UAM
                    "csf" -> RemoraStatusData.AutosensType.CSF
                    else -> RemoraStatusData.AutosensType.NEUTRAL
                }
            )
        }

    private fun getInsulinDataFor(time: Long) =
        profileFunction.getProfile(time)?.let { profile ->
            val iobTotal = iobCobCalculator.calculateFromTreatmentsAndTemps(time, profile)
            val baseBasalIob = iobCobCalculator.calculateAbsoluteIobFromBaseBasals(time)
            val absIob = IobTotal.combine(iobTotal, baseBasalIob).iob
            RemoraStatusData.InsulinData(
                iob = iobTotal.iob.toFloat(),
                absoluteIob = absIob.toFloat(),
                insulinActivity = iobTotal.activity.toFloat()
            )
        }

    private fun getBasalData(start: Long, end: Long): List<BasalDataPoint> =
        generateSequence(start) { it + T.mins(5).msecs() }
            .takeWhile { it <= end }
            .mapNotNull { time ->
                val profile = profileFunction.getProfile(time)
                if (profile == null) return@mapNotNull null
                val basalData = iobCobCalculator.getBasalData(profile, time)
                BasalDataPoint(
                    timestamp = Instant.fromEpochMilliseconds(time),
                    baselineBasal = basalData.basal.toFloat(),
                    tempBasalAbsolute = if (basalData.isTempBasalRunning) basalData.tempBasalAbsolute.toFloat() else null
                )
            }
            .fold(mutableListOf()) { acc, dp ->
                if (acc.isEmpty() ||
                    acc.last().baselineBasal != dp.baselineBasal ||
                    acc.last().tempBasalAbsolute != dp.tempBasalAbsolute
                ) acc += dp
                acc
            }

    private fun getTargetData(start: Long, end: Long): List<TargetDataPoint> {
        val profile = profileFunction.getProfile() ?: return emptyList()
        return generateSequence(start) { it + T.mins(1).msecs() }
            .takeWhile { it <= end }
            .map { time ->
                val tt = persistenceLayer.getTemporaryTargetActiveAt(time)
                TargetDataPoint(
                    timestamp = Instant.fromEpochMilliseconds(time),
                    target = (tt?.target() ?: ((profile.getTargetLowMgdl(time) + profile.getTargetHighMgdl(time)) / 2)).toFloat()
                )
            }
            .fold(mutableListOf()) { acc, dp ->
                if (acc.isEmpty() || acc.last().target != dp.target) acc += dp
                acc
            }
    }

    private fun getBgReadings(): List<BgReading> =
        iobCobCalculator.ads.bgReadings.sortedBy { it.timestamp }.map { bgReading ->
            BgReading(
                timestamp = Instant.fromEpochMilliseconds(bgReading.timestamp),
                id = bgReading.id,
                value = bgReading.value.toFloat(),
                trendArrow = convertAapsTrendToRemoraTrend(bgReading.trendArrow)
            )
        }

    private fun getPredictions(): List<Prediction> {
        val lastLoopRun = if (config.APS) loop.lastRun else null
        val predictions = if (lastLoopRun?.request?.hasPredictions == true) lastLoopRun.constraintsProcessed?.predictionsAsGv?.sortedBy { it.timestamp } ?: emptyList() else emptyList()
        return predictions.map { prediction ->
            Prediction(
                timestamp = Instant.fromEpochMilliseconds(prediction.timestamp),
                value = prediction.value.toFloat(),
                type = when (prediction.sourceSensor) {
                    SourceSensor.IOB_PREDICTION   -> RemoraStatusData.PredictionType.IOB
                    SourceSensor.COB_PREDICTION   -> RemoraStatusData.PredictionType.COB
                    SourceSensor.A_COB_PREDICTION -> RemoraStatusData.PredictionType.A_COB
                    SourceSensor.UAM_PREDICTION   -> RemoraStatusData.PredictionType.UAM
                    SourceSensor.ZT_PREDICTION    -> RemoraStatusData.PredictionType.ZT
                    else                          -> error("Invalid prediction type")
                }
            )
        }
    }

    private fun getBoluses(start: Long, end: Long): List<Bolus> =
        persistenceLayer.getBolusesFromTimeToTime(start, end, true).map { bolus ->
            Bolus(
                timestamp = Instant.fromEpochMilliseconds(bolus.timestamp),
                id = bolus.id,
                amount = bolus.amount.toFloat(),
                type = when (bolus.type) {
                    BS.Type.NORMAL  -> RemoraStatusData.BolusType.NORMAL
                    BS.Type.SMB     -> RemoraStatusData.BolusType.SMB
                    BS.Type.PRIMING -> RemoraStatusData.BolusType.PRIMING
                }
            )
        }

    private fun getCarbs(start: Long, end: Long): List<CarbEntry> =
        persistenceLayer.getCarbsFromTimeToTimeNotExpanded(start, end, true).map { carbs ->
            CarbEntry(
                timestamp = Instant.fromEpochMilliseconds(carbs.timestamp),
                id = carbs.id,
                amount = carbs.amount.toFloat(),
                duration = carbs.duration.milliseconds
            )
        }

    private fun getProfileSwitches(start: Long, end: Long) =
        persistenceLayer.getEffectiveProfileSwitchesFromTimeToTime(start, end, true).map { profileSwitch ->
            ProfileSwitch(
                timestamp = Instant.fromEpochMilliseconds(profileSwitch.timestamp),
                id = profileSwitch.id,
                profileName = profileSwitch.originalProfileName,
                timeshift = profileSwitch.originalTimeshift.milliseconds,
                percentage = profileSwitch.originalPercentage,
                duration = profileSwitch.originalDuration.milliseconds
            )
        }

    private fun getExtendedBoluses(start: Long, end: Long): List<ExtendedBolus> =
        persistenceLayer.getExtendedBolusesStartingFromTimeToTime(start, end, true)
            .filter { it.duration != 0L }
            .map { extendedBolus ->
                ExtendedBolus(
                    timestamp = Instant.fromEpochMilliseconds(extendedBolus.timestamp),
                    id = extendedBolus.id,
                    amount = extendedBolus.amount.toFloat(),
                    duration = extendedBolus.duration.milliseconds
                )
            }
}
