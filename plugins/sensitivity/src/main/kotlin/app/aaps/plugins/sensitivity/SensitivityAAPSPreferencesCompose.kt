package app.aaps.plugins.sensitivity

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveDoublePreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen

/**
 * Compose implementation of Sensitivity AAPS preferences.
 */
class SensitivityAAPSPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.sensitivity_aaps

    // Main content shown at top level
    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
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

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Advanced settings subscreen
        PreferenceSubScreen(
            key = "absorption_aaps_advanced",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            summaryItems = listOf(
                R.string.openapsama_autosens_max,
                R.string.openapsama_autosens_min
            )
        ) { _ ->
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
    )
}
