package app.aaps.plugins.aps.openAPSSMB

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.IntentKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveDoublePreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveUrlPreferenceItem
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.plugins.aps.R

/**
 * Compose implementation of OpenAPS SMB preferences.
 */
class OpenAPSSMBPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val linkToDocsUrl: String? = null
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // OpenAPS SMB settings category
        val smbSettingsKey = "${keyPrefix}_openapssmb_settings"
        item {
            val isExpanded = sectionState?.isExpanded(smbSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.openapssmb,
                summaryItems = listOf(
                    R.string.openapsma_max_basal_title,
                    R.string.openapssmb_max_iob_title,
                    R.string.use_dynamic_sensitivity_title,
                    R.string.enable_smb,
                    R.string.enable_uam
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(smbSettingsKey) }
            ) {
            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.ApsMaxBasal,
                titleResId = R.string.openapsma_max_basal_title
            )

            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.ApsSmbMaxIob,
                titleResId = R.string.openapssmb_max_iob_title
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsUseDynamicSensitivity,
                titleResId = R.string.use_dynamic_sensitivity_title,
                summaryResId = R.string.use_dynamic_sensitivity_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsUseAutosens,
                titleResId = R.string.openapsama_use_autosens
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.ApsDynIsfAdjustmentFactor,
                titleResId = R.string.dyn_isf_adjust_title
            )

            // Note: UnitPreference (ApsLgsThreshold) not yet supported in Compose

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsDynIsfAdjustSensitivity,
                titleResId = R.string.dynisf_adjust_sensitivity,
                summaryResId = R.string.dynisf_adjust_sensitivity_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsSensitivityRaisesTarget,
                titleResId = R.string.sensitivity_raises_target_title,
                summaryResId = R.string.sensitivity_raises_target_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsResistanceLowersTarget,
                titleResId = R.string.resistance_lowers_target_title,
                summaryResId = R.string.resistance_lowers_target_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsUseSmb,
                titleResId = R.string.enable_smb,
                summaryResId = R.string.enable_smb_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsUseSmbWithHighTt,
                titleResId = R.string.enable_smb_with_high_temp_target,
                summaryResId = R.string.enable_smb_with_high_temp_target_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsUseSmbAlways,
                titleResId = R.string.enable_smb_always,
                summaryResId = R.string.enable_smb_always_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsUseSmbWithCob,
                titleResId = R.string.enable_smb_with_cob,
                summaryResId = R.string.enable_smb_with_cob_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsUseSmbWithLowTt,
                titleResId = R.string.enable_smb_with_temp_target,
                summaryResId = R.string.enable_smb_with_temp_target_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsUseSmbAfterCarbs,
                titleResId = R.string.enable_smb_after_carbs,
                summaryResId = R.string.enable_smb_after_carbs_summary
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.ApsMaxSmbFrequency,
                titleResId = R.string.smb_interval_summary
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.ApsMaxMinutesOfBasalToLimitSmb,
                titleResId = R.string.smb_max_minutes_summary
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.ApsUamMaxMinutesOfBasalToLimitSmb,
                titleResId = R.string.uam_smb_max_minutes_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsUseUam,
                titleResId = R.string.enable_uam,
                summaryResId = R.string.enable_uam_summary
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.ApsCarbsRequestThreshold,
                titleResId = R.string.carbs_req_threshold
            )
            }
        }

        // Advanced settings category
        val advancedSettingsKey = "${keyPrefix}_absorption_smb_advanced"
        item {
            val isExpanded = sectionState?.isExpanded(advancedSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
                summaryItems = listOf(
                    R.string.openapsama_link_to_preference_json_doc_txt,
                    R.string.always_use_short_avg,
                    R.string.openapsama_max_daily_safety_multiplier,
                    R.string.openapsama_current_basal_safety_multiplier
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(advancedSettingsKey) }
            ) {
            linkToDocsUrl?.let { url ->
                AdaptiveUrlPreferenceItem(
                    preferences = preferences,
                    intentKey = IntentKey.ApsLinkToDocs,
                    titleResId = R.string.openapsama_link_to_preference_json_doc_txt,
                    url = url
                )
            }

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsAlwaysUseShortDeltas,
                titleResId = R.string.always_use_short_avg,
                summaryResId = R.string.always_use_short_avg_summary
            )

            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.ApsMaxDailyMultiplier,
                titleResId = R.string.openapsama_max_daily_safety_multiplier
            )

            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.ApsMaxCurrentBasalMultiplier,
                titleResId = R.string.openapsama_current_basal_safety_multiplier
            )
            }
        }
    }
}
