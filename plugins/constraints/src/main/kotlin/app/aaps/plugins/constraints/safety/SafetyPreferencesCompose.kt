package app.aaps.plugins.constraints.safety

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.utils.HardLimits
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withEntries
import app.aaps.core.ui.compose.preference.AdaptivePreferenceListForListKeys
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.navigable.PreferenceSubScreen
import app.aaps.plugins.constraints.R

/**
 * Compose implementation of Safety preferences.
 */
class SafetyPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val hardLimits: HardLimits
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.safety

    override val mainKeys: List<PreferenceKey> = listOf(
        StringKey.SafetyAge.withEntries(
            hardLimits.ageEntryValues().zip(hardLimits.ageEntries()).associate { it.first.toString() to it.second.toString() }
        ),
        DoubleKey.SafetyMaxBolus,
        IntKey.SafetyMaxCarbs
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceListForListKeys(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
