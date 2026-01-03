package app.aaps.core.keys

import app.aaps.core.keys.interfaces.BooleanPreferenceKey
import app.aaps.core.keys.interfaces.IntPreferenceKey
import app.aaps.core.keys.interfaces.PreferenceEnabledCondition

enum class IntKey(
    override val key: String,
    override val defaultValue: Int,
    override val min: Int,
    override val max: Int,
    override val titleResId: Int,
    override val summaryResId: Int? = null,
    override val preferenceType: PreferenceType = PreferenceType.TEXT_FIELD,
    override val entries: Map<Int, Int> = emptyMap(),
    override val defaultedBySM: Boolean = false,
    override val calculatedDefaultValue: Boolean = false,
    override val showInApsMode: Boolean = true,
    override val showInNsClientMode: Boolean = true,
    override val showInPumpControlMode: Boolean = true,
    override val dependency: BooleanPreferenceKey? = null,
    override val negativeDependency: BooleanPreferenceKey? = null,
    override val hideParentScreenIfHidden: Boolean = false,
    override val engineeringModeOnly: Boolean = false,
    override val exportable: Boolean = true,
    override val enabledCondition: PreferenceEnabledCondition = PreferenceEnabledCondition.ALWAYS
) : IntPreferenceKey {

    OverviewCarbsButtonIncrement1(key = "carbs_button_increment_1", defaultValue = 5, min = -50, max = 50, titleResId = R.string.pref_title_carbs_button_increment_1, defaultedBySM = true, dependency = BooleanKey.OverviewShowCarbsButton),
    OverviewCarbsButtonIncrement2(key = "carbs_button_increment_2", defaultValue = 10, min = -50, max = 50, titleResId = R.string.pref_title_carbs_button_increment_2, defaultedBySM = true, dependency = BooleanKey.OverviewShowCarbsButton),
    OverviewCarbsButtonIncrement3(key = "carbs_button_increment_3", defaultValue = 20, min = -50, max = 50, titleResId = R.string.pref_title_carbs_button_increment_3, defaultedBySM = true, dependency = BooleanKey.OverviewShowCarbsButton),
    OverviewEatingSoonDuration(key = "eatingsoon_duration", defaultValue = 45, min = 15, max = 120, titleResId = R.string.pref_title_eating_soon_duration, defaultedBySM = true, hideParentScreenIfHidden = true),
    OverviewActivityDuration(key = "activity_duration", defaultValue = 90, min = 15, max = 600, titleResId = R.string.pref_title_activity_duration, defaultedBySM = true),
    OverviewHypoDuration(key = "hypo_duration", defaultValue = 60, min = 15, max = 180, titleResId = R.string.pref_title_hypo_duration, defaultedBySM = true),
    OverviewCageWarning(key = "statuslights_cage_warning", defaultValue = 48, min = 24, max = 240, titleResId = R.string.pref_title_cage_warning, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewCageCritical(key = "statuslights_cage_critical", defaultValue = 72, min = 24, max = 240, titleResId = R.string.pref_title_cage_critical, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewSageWarning(key = "statuslights_sage_warning", defaultValue = 216, min = 24, max = 720, titleResId = R.string.pref_title_sage_warning, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewSageCritical(key = "statuslights_sage_critical", defaultValue = 240, min = 24, max = 720, titleResId = R.string.pref_title_sage_critical, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewSbatWarning(key = "statuslights_sbat_warning", defaultValue = 25, min = 0, max = 100, titleResId = R.string.pref_title_sbat_warning, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewSbatCritical(key = "statuslights_sbat_critical", defaultValue = 5, min = 0, max = 100, titleResId = R.string.pref_title_sbat_critical, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewBageWarning(key = "statuslights_bage_warning", defaultValue = 216, min = 24, max = 1000, titleResId = R.string.pref_title_bage_warning, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewBageCritical(key = "statuslights_bage_critical", defaultValue = 240, min = 24, max = 1000, titleResId = R.string.pref_title_bage_critical, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewResWarning(key = "statuslights_res_warning", defaultValue = 80, min = 0, max = 300, titleResId = R.string.pref_title_res_warning, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewResCritical(key = "statuslights_res_critical", defaultValue = 10, min = 0, max = 300, titleResId = R.string.pref_title_res_critical, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewBattWarning(key = "statuslights_bat_warning", defaultValue = 51, min = 0, max = 100, titleResId = R.string.pref_title_batt_warning, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewBattCritical(key = "statuslights_bat_critical", defaultValue = 26, min = 0, max = 100, titleResId = R.string.pref_title_batt_critical, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights),
    OverviewBolusPercentage(key = "boluswizard_percentage", defaultValue = 100, min = 10, max = 100, titleResId = R.string.pref_title_bolus_percentage),
    OverviewResetBolusPercentageTime(key = "key_reset_boluswizard_percentage_time", defaultValue = 16, min = 6, max = 120, titleResId = R.string.pref_title_reset_bolus_percentage_time, defaultedBySM = true, engineeringModeOnly = true),
    ProtectionTimeout(key = "prot2ection_timeout", defaultValue = 1, min = 1, max = 180, titleResId = R.string.pref_title_protection_timeout, defaultedBySM = true),
    ProtectionTypeSettings(key = "settings_protection", defaultValue = 0, min = 0, max = 5, titleResId = R.string.pref_title_protection_type_settings, preferenceType = PreferenceType.LIST),
    ProtectionTypeApplication(key = "application_protection", defaultValue = 0, min = 0, max = 5, titleResId = R.string.pref_title_protection_type_application, preferenceType = PreferenceType.LIST),
    ProtectionTypeBolus(key = "bolus_protection", defaultValue = 0, min = 0, max = 5, titleResId = R.string.pref_title_protection_type_bolus, preferenceType = PreferenceType.LIST),
    SafetyMaxCarbs(key = "treatmentssafety_maxcarbs", defaultValue = 48, min = 1, max = 200, titleResId = R.string.pref_title_max_carbs),
    LoopOpenModeMinChange(key = "loop_openmode_min_change", defaultValue = 30, min = 0, max = 50, titleResId = R.string.pref_title_open_mode_min_change, defaultedBySM = true),
    ApsMaxSmbFrequency(key = "smbinterval", defaultValue = 3, min = 1, max = 10, titleResId = R.string.pref_title_smb_frequency, defaultedBySM = true, dependency = BooleanKey.ApsUseSmb),
    ApsMaxMinutesOfBasalToLimitSmb(key = "smbmaxminutes", defaultValue = 30, min = 15, max = 120, titleResId = R.string.pref_title_smb_max_minutes, defaultedBySM = true, dependency = BooleanKey.ApsUseSmb),
    ApsUamMaxMinutesOfBasalToLimitSmb(key = "uamsmbmaxminutes", defaultValue = 30, min = 15, max = 120, titleResId = R.string.pref_title_uam_smb_max_minutes, defaultedBySM = true, dependency = BooleanKey.ApsUseSmb),
    ApsCarbsRequestThreshold(key = "carbsReqThreshold", defaultValue = 1, min = 1, max = 100, titleResId = R.string.pref_title_carbs_request_threshold, defaultedBySM = true),
    ApsAutoIsfHalfBasalExerciseTarget(key = "half_basal_exercise_target", defaultValue = 160, min = 120, max = 200, titleResId = R.string.pref_title_half_basal_exercise_target, defaultedBySM = true),
    ApsAutoIsfIobThPercent(key = "iob_threshold_percent", defaultValue = 100, min = 10, max = 100, titleResId = R.string.pref_title_iob_threshold_percent, defaultedBySM = true),
    ApsDynIsfAdjustmentFactor(key = "DynISFAdjust", defaultValue = 100, min = 1, max = 300, titleResId = R.string.pref_title_dynisf_adjustment_factor, dependency = BooleanKey.ApsUseDynamicSensitivity),
    AutosensPeriod(key = "openapsama_autosens_period", defaultValue = 24, min = 4, max = 24, titleResId = R.string.pref_title_autosens_period, calculatedDefaultValue = true),
    MaintenanceLogsAmount(key = "maintenance_logs_amount", defaultValue = 2, min = 1, max = 10, titleResId = R.string.pref_title_logs_amount, defaultedBySM = true),
    AlertsStaleDataThreshold(key = "missed_bg_readings_threshold", defaultValue = 30, min = 15, max = 10000, titleResId = R.string.pref_title_stale_data_threshold, defaultedBySM = true, dependency = BooleanKey.AlertMissedBgReading),
    AlertsPumpUnreachableThreshold(key = "pump_unreachable_threshold", defaultValue = 30, min = 30, max = 300, titleResId = R.string.pref_title_pump_unreachable_threshold, defaultedBySM = true, dependency = BooleanKey.AlertPumpUnreachable),
    InsulinOrefPeak(key = "insulin_oref_peak", defaultValue = 75, min = 35, max = 120, titleResId = R.string.pref_title_insulin_oref_peak, hideParentScreenIfHidden = true),

    AutotuneDefaultTuneDays(key = "autotune_default_tune_days", defaultValue = 5, min = 1, max = 30, titleResId = R.string.pref_title_autotune_days),

    SmsRemoteBolusDistance(
        key = "smscommunicator_remotebolusmindistance",
        defaultValue = 15,
        min = 3,
        max = 60,
        titleResId = R.string.pref_title_sms_remote_bolus_distance,
        // Enabled only when multiple phone numbers are configured (2FA requirement)
        enabledCondition = PreferenceEnabledCondition { ctx ->
            val allowedNumbers = ctx.preferences.get(StringKey.SmsAllowedNumbers)
            allowedNumbers.split(";").filter { it.trim().isNotEmpty() }.size >= 2
        }
    ),

    BgSourceRandomInterval(key = "randombg_interval_min", defaultValue = 5, min = 1, max = 15, titleResId = R.string.pref_title_random_bg_interval, defaultedBySM = true),
    NsClientAlarmStaleData(key = "ns_alarm_stale_data_value", defaultValue = 16, min = 15, max = 120, titleResId = R.string.pref_title_alarm_stale_data),
    NsClientUrgentAlarmStaleData(key = "ns_alarm_urgent_stale_data_value", defaultValue = 31, min = 30, max = 180, titleResId = R.string.pref_title_urgent_alarm_stale_data),

    SiteRotationUserProfile(key = "site_rotation_user_profile", defaultValue = 0, min = 0, max = 2, titleResId = R.string.pref_title_site_rotation_profile),
}