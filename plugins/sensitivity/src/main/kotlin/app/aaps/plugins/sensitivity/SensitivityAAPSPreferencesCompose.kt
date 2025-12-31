package app.aaps.plugins.sensitivity

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveDoublePreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState

/**
 * Compose implementation of Sensitivity AAPS preferences.
 */
class SensitivityAAPSPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Absorption settings category
        val sectionKey1 = "${keyPrefix}_sensitivity_aaps_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey1) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.absorption_settings_title,
                summaryItems = listOf(
                    R.string.absorption_max_time_title,
                    R.string.openapsama_autosens_period
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey1) }
            ) {
                AdaptiveDoublePreferenceItem(
                    preferences = preferences,
                    config = config,
                    doubleKey = DoubleKey.AbsorptionMaxTime,
                    titleResId = R.string.absorption_max_time_title
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = IntKey.AutosensPeriod,
                    titleResId = R.string.openapsama_autosens_period
                )
            }
        }

        // Advanced settings section (collapsible like original subscreen)
        val sectionKey2 = "${keyPrefix}_absorption_aaps_advanced"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey2) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
                summaryItems = listOf(
                    R.string.openapsama_autosens_max,
                    R.string.openapsama_autosens_min
                ),
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
