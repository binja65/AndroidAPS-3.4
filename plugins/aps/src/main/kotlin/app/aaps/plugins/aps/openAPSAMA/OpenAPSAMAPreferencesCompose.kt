package app.aaps.plugins.aps.openAPSAMA

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntentKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveDoublePreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveUrlPreferenceItem
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.plugins.aps.R

/**
 * Compose implementation of OpenAPS AMA preferences.
 */
class OpenAPSAMAPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val linkToDocsUrl: String? = null
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // OpenAPS AMA settings category
        val amaSettingsKey = "${keyPrefix}_openapsma_settings"
        item {
            val isExpanded = sectionState?.isExpanded(amaSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.openapsama,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(amaSettingsKey) }
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
                doubleKey = DoubleKey.ApsAmaMaxIob,
                titleResId = R.string.openapsma_max_iob_title
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsUseAutosens,
                titleResId = R.string.openapsama_use_autosens
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.ApsAmaAutosensAdjustTargets,
                titleResId = R.string.openapsama_autosens_adjust_targets,
                summaryResId = R.string.openapsama_autosens_adjust_targets_summary
            )

            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.ApsAmaMin5MinCarbsImpact,
                titleResId = R.string.openapsama_min_5m_carb_impact
            )
            }
        }

        // Advanced settings category
        val advancedSettingsKey = "${keyPrefix}_absorption_ama_advanced"
        item {
            val isExpanded = sectionState?.isExpanded(advancedSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
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

            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.ApsAmaBolusSnoozeDivisor,
                titleResId = R.string.openapsama_bolus_snooze_dia_divisor
            )
            }
        }
    }
}
