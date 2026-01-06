package app.aaps.pump.eopatch

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withEntries
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.eopatch.keys.EopatchBooleanKey
import app.aaps.pump.eopatch.keys.EopatchIntKey

/**
 * Compose implementation of Eopatch preferences.
 */
class EopatchPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.eopatch

    override val mainKeys: List<PreferenceKey> = listOf(
        EopatchIntKey.LowReservoirReminder.withEntries((10..50 step 5).associateWith { "$it U" }),
        EopatchIntKey.ExpirationReminder.withEntries((1..24).associateWith { "$it hr" }),
        EopatchBooleanKey.BuzzerReminder
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
