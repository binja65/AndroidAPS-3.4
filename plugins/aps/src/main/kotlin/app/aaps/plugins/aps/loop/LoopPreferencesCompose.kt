package app.aaps.plugins.aps.loop

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.aps.R

/**
 * Compose implementation of Loop preferences.
 */
class LoopPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.core.ui.R.string.loop

    override val summaryItems: List<Int> = listOf(
        R.string.loop_open_mode_min_change
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.LoopOpenModeMinChange,
            titleResId = R.string.loop_open_mode_min_change
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
