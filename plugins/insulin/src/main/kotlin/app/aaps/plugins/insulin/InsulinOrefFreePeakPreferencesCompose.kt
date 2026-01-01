package app.aaps.plugins.insulin

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen

/**
 * Compose implementation of Insulin Free Peak preferences.
 */
class InsulinOrefFreePeakPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.free_peak_oref

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.InsulinOrefPeak,
            titleResId = R.string.insulin_peak_time
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
