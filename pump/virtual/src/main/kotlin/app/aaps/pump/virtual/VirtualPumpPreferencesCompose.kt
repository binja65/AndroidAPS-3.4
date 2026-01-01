package app.aaps.pump.virtual

import androidx.compose.runtime.Composable
import app.aaps.core.data.pump.defs.PumpType
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen

/**
 * Compose implementation of Virtual Pump preferences.
 */
class VirtualPumpPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    // Build pump type entries dynamically
    private val pumpTypeEntries = PumpType.entries
        .filter { it.description != "USER" }
        .sortedBy { it.description }
        .associate { it.description to it.description }

    override val titleResId: Int = app.aaps.core.ui.R.string.virtual_pump

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.VirtualPumpType,
            titleResId = R.string.virtual_pump_type,
            entries = pumpTypeEntries
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.VirtualPumpStatusUpload,
            titleResId = app.aaps.core.ui.R.string.virtualpump_uploadstatus_title
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
