package app.aaps.core.keys

import app.aaps.core.keys.interfaces.BooleanPreferenceKey
import app.aaps.core.keys.interfaces.DoublePreferenceKey

enum class DoubleKey(
    override val key: String,
    override val defaultValue: Double,
    override val min: Double,
    override val max: Double,
    override val titleResId: Int,
    override val summaryResId: Int? = null,
    override val preferenceType: PreferenceType = PreferenceType.TEXT_FIELD,
    override val defaultedBySM: Boolean = false,
    override val calculatedBySM: Boolean = false,
    override val showInApsMode: Boolean = true,
    override val showInNsClientMode: Boolean = true,
    override val showInPumpControlMode: Boolean = true,
    override val dependency: BooleanPreferenceKey? = null,
    override val negativeDependency: BooleanPreferenceKey? = null,
    override val hideParentScreenIfHidden: Boolean = false,
    override val exportable: Boolean = true
) : DoublePreferenceKey {

    OverviewInsulinButtonIncrement1(key = "insulin_button_increment_1", defaultValue = 0.5, min = -5.0, max = 5.0, titleResId = R.string.pref_title_insulin_button_increment_1, defaultedBySM = true, dependency = BooleanKey.OverviewShowInsulinButton),
    OverviewInsulinButtonIncrement2(key = "insulin_button_increment_2", defaultValue = 1.0, min = -5.0, max = 5.0, titleResId = R.string.pref_title_insulin_button_increment_2, defaultedBySM = true, dependency = BooleanKey.OverviewShowInsulinButton),
    OverviewInsulinButtonIncrement3(key = "insulin_button_increment_3", defaultValue = 2.0, min = -5.0, max = 5.0, titleResId = R.string.pref_title_insulin_button_increment_3, defaultedBySM = true, dependency = BooleanKey.OverviewShowInsulinButton),
    ActionsFillButton1(key = "fill_button1", defaultValue = 0.3, min = 0.05, max = 20.0, titleResId = R.string.pref_title_fill_button_1, defaultedBySM = true, hideParentScreenIfHidden = true),
    ActionsFillButton2(key = "fill_button2", defaultValue = 0.0, min = 0.05, max = 20.0, titleResId = R.string.pref_title_fill_button_2, defaultedBySM = true),
    ActionsFillButton3(key = "fill_button3", defaultValue = 0.0, min = 0.05, max = 20.0, titleResId = R.string.pref_title_fill_button_3, defaultedBySM = true),
    SafetyMaxBolus(key = "treatmentssafety_maxbolus", defaultValue = 3.0, min = 0.1, max = 60.0, titleResId = R.string.pref_title_max_bolus),
    ApsMaxBasal(key = "openapsma_max_basal", defaultValue = 1.0, min = 0.1, max = 25.0, titleResId = R.string.pref_title_max_basal, defaultedBySM = true, calculatedBySM = true),
    ApsSmbMaxIob(key = "openapsmb_max_iob", defaultValue = 3.0, min = 0.0, max = 70.0, titleResId = R.string.pref_title_smb_max_iob, defaultedBySM = true, calculatedBySM = true),
    ApsAmaMaxIob(key = "openapsma_max_iob", defaultValue = 1.5, min = 0.0, max = 25.0, titleResId = R.string.pref_title_ama_max_iob, defaultedBySM = true, calculatedBySM = true),
    ApsMaxDailyMultiplier(key = "openapsama_max_daily_safety_multiplier", defaultValue = 3.0, min = 1.0, max = 10.0, titleResId = R.string.pref_title_max_daily_multiplier, defaultedBySM = true),
    ApsMaxCurrentBasalMultiplier(key = "openapsama_current_basal_safety_multiplier", defaultValue = 4.0, min = 1.0, max = 10.0, titleResId = R.string.pref_title_current_basal_multiplier, defaultedBySM = true),
    ApsAmaBolusSnoozeDivisor(key = "bolussnooze_dia_divisor", defaultValue = 2.0, min = 1.0, max = 10.0, titleResId = R.string.pref_title_bolus_snooze_divisor, defaultedBySM = true),
    ApsAmaMin5MinCarbsImpact(key = "openapsama_min_5m_carbimpact", defaultValue = 3.0, min = 1.0, max = 12.0, titleResId = R.string.pref_title_ama_min_5m_carbs_impact, defaultedBySM = true),
    ApsSmbMin5MinCarbsImpact(key = "openaps_smb_min_5m_carbimpact", defaultValue = 8.0, min = 1.0, max = 12.0, titleResId = R.string.pref_title_smb_min_5m_carbs_impact, defaultedBySM = true),
    AbsorptionCutOff(key = "absorption_cutoff", defaultValue = 6.0, min = 4.0, max = 10.0, titleResId = R.string.pref_title_absorption_cutoff),
    AbsorptionMaxTime(key = "absorption_maxtime", defaultValue = 6.0, min = 4.0, max = 10.0, titleResId = R.string.pref_title_absorption_maxtime),
    AutosensMin(key = "autosens_min", defaultValue = 0.7, min = 0.1, max = 1.0, titleResId = R.string.pref_title_autosens_min, defaultedBySM = true, hideParentScreenIfHidden = true),
    AutosensMax(key = "autosens_max", defaultValue = 1.2, min = 0.5, max = 3.0, titleResId = R.string.pref_title_autosens_max, defaultedBySM = true),
    ApsAutoIsfMin(key = "autoISF_min", defaultValue = 1.0, min = 0.3, max = 1.0, titleResId = R.string.pref_title_autoisf_min, defaultedBySM = true),
    ApsAutoIsfMax(key = "autoISF_max", defaultValue = 1.0, min = 1.0, max = 3.0, titleResId = R.string.pref_title_autoisf_max, defaultedBySM = true),
    ApsAutoIsfBgAccelWeight(key = "bgAccel_ISF_weight", defaultValue = 0.0, min = 0.0, max = 1.0, titleResId = R.string.pref_title_bg_accel_weight, defaultedBySM = true),
    ApsAutoIsfBgBrakeWeight(key = "bgBrake_ISF_weight", defaultValue = 0.0, min = 0.0, max = 1.0, titleResId = R.string.pref_title_bg_brake_weight, defaultedBySM = true),
    ApsAutoIsfLowBgWeight(key = "lower_ISFrange_weight", defaultValue = 0.0, min = 0.0, max = 2.0, titleResId = R.string.pref_title_low_bg_weight, defaultedBySM = true),
    ApsAutoIsfHighBgWeight(key = "higher_ISFrange_weight", defaultValue = 0.0, min = 0.0, max = 2.0, titleResId = R.string.pref_title_high_bg_weight, defaultedBySM = true),
    ApsAutoIsfSmbDeliveryRatioBgRange(key = "openapsama_smb_delivery_ratio_bg_range", defaultValue = 0.0, min = 0.0, max = 100.0, titleResId = R.string.pref_title_smb_delivery_ratio_bg_range, defaultedBySM = true),
    ApsAutoIsfPpWeight(key = "pp_ISF_weight", defaultValue = 0.0, min = 0.0, max = 1.0, titleResId = R.string.pref_title_pp_weight, defaultedBySM = true),
    ApsAutoIsfDuraWeight(key = "dura_ISF_weight", defaultValue = 0.0, min = 0.0, max = 3.0, titleResId = R.string.pref_title_dura_weight, defaultedBySM = true),
    ApsAutoIsfSmbDeliveryRatio(key = "openapsama_smb_delivery_ratio", defaultValue = 0.5, min = 0.5, max = 1.0, titleResId = R.string.pref_title_smb_delivery_ratio, defaultedBySM = true),
    ApsAutoIsfSmbDeliveryRatioMin(key = "openapsama_smb_delivery_ratio_min", defaultValue = 0.5, min = 0.5, max = 1.0, titleResId = R.string.pref_title_smb_delivery_ratio_min, defaultedBySM = true),
    ApsAutoIsfSmbDeliveryRatioMax(key = "openapsama_smb_delivery_ratio_max", defaultValue = 0.5, min = 0.5, max = 1.0, titleResId = R.string.pref_title_smb_delivery_ratio_max, defaultedBySM = true),
    ApsAutoIsfSmbMaxRangeExtension(key = "openapsama_smb_max_range_extension", defaultValue = 1.0, min = 1.0, max = 5.0, titleResId = R.string.pref_title_smb_max_range_extension, defaultedBySM = true),

}