package app.aaps.pump.virtual

import androidx.compose.runtime.Composable
import app.aaps.core.data.pump.defs.PumpType
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withEntries
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
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

    override val titleResId: Int = app.aaps.core.ui.R.string.virtual_pump

    override val mainKeys: List<PreferenceKey> = listOf(
        StringKey.VirtualPumpType.withEntries(
            PumpType.entries
                .filter { it.description != "USER" }
                .sortedBy { it.description }
                .associate { it.description to it.description }
        ),
        BooleanKey.VirtualPumpStatusUpload
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
