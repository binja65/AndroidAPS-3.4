package app.aaps.plugins.constraints.safety

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.utils.HardLimits
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withEntries
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
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
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
