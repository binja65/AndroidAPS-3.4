package app.aaps.plugins.sensitivity

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveDoublePreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen

/**
 * Compose implementation of Sensitivity Oref1 preferences.
 */
class SensitivityOref1PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.sensitivity_oref1

    // Main content shown at top level
    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
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

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Advanced settings subscreen
        PreferenceSubScreen(
            key = "absorption_oref1_advanced",
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
