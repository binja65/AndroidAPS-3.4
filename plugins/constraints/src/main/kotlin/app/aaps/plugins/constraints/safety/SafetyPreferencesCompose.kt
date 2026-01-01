package app.aaps.plugins.constraints.safety

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.utils.HardLimits
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.constraints.R

/**
 * Compose implementation of Safety preferences.
 * Note: Patient age requires dynamic entries from HardLimits.
 */
class SafetyPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val hardLimits: HardLimits
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.safety

    override val mainKeys: List<PreferenceKey> = listOf(
        StringKey.SafetyAge,
        DoubleKey.SafetyMaxBolus,
        IntKey.SafetyMaxCarbs
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Patient age - requires dynamic entries from HardLimits
        val ageEntries = hardLimits.ageEntryValues().zip(hardLimits.ageEntries()).associate {
            it.first.toString() to it.second.toString()
        }
        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.SafetyAge,
            titleResId = app.aaps.core.ui.R.string.patient_type,
            entries = ageEntries
        )

        // Max bolus and carbs - can use key-based
        AdaptivePreferenceList(
            keys = listOf(DoubleKey.SafetyMaxBolus, IntKey.SafetyMaxCarbs),
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
