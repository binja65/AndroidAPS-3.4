package app.aaps.compose.preferences

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen

/**
 * Compose implementation of Pump preferences.
 */
class PumpPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.core.ui.R.string.pump

    override val summaryItems: List<Int> = listOf(
        app.aaps.core.ui.R.string.btwatchdog_title
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // BT Watchdog
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.PumpBtWatchdog,
            titleResId = app.aaps.core.ui.R.string.btwatchdog_title,
            summaryResId = app.aaps.core.ui.R.string.btwatchdog_summary
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
