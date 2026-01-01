package app.aaps.plugins.sensitivity

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen

/**
 * Compose implementation of Sensitivity AAPS preferences.
 * Simply provides list of keys - UI is auto-generated.
 */
class SensitivityAAPSPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.sensitivity_aaps

    override val mainKeys: List<PreferenceKey> = listOf(
        DoubleKey.AbsorptionMaxTime,
        IntKey.AutosensPeriod
    )

    private val advancedKeys: List<PreferenceKey> = listOf(
        DoubleKey.AutosensMax,
        DoubleKey.AutosensMin
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        PreferenceSubScreen(
            key = "absorption_aaps_advanced",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            keys = advancedKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = advancedKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
