package app.aaps.plugins.sensitivity

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveDoublePreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState

/**
 * Compose implementation of Sensitivity Oref1 preferences.
 */
class SensitivityOref1PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Absorption settings category
        val sectionKey1 = "${keyPrefix}_sensitivity_oref1_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey1) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.absorption_settings_title,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey1) }
            ) {
                AdaptiveDoublePreferenceItem(
                    preferences = preferences,
                    config = config,
                    doubleKey = DoubleKey.ApsSmbMin5MinCarbsImpact,
                    titleResId = R.string.openapsama_min_5m_carb_impact
                )

                AdaptiveDoublePreferenceItem(
                    preferences = preferences,
                    config = config,
                    doubleKey = DoubleKey.AbsorptionCutOff,
                    titleResId = R.string.absorption_cutoff_title
                )
            }
        }

        // Advanced settings category
        val sectionKey2 = "${keyPrefix}_absorption_oref1_advanced"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey2) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey2) }
            ) {
                AdaptiveDoublePreferenceItem(
                    preferences = preferences,
                    config = config,
                    doubleKey = DoubleKey.AutosensMax,
                    titleResId = R.string.openapsama_autosens_max
                )

                AdaptiveDoublePreferenceItem(
                    preferences = preferences,
                    config = config,
                    doubleKey = DoubleKey.AutosensMin,
                    titleResId = R.string.openapsama_autosens_min
                )
            }
        }
    }
}
