package app.aaps.plugins.constraints.safety

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.utils.HardLimits
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveDoublePreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
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

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Patient age preference
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

        // Max bolus
        AdaptiveDoublePreferenceItem(
            preferences = preferences,
            config = config,
            doubleKey = DoubleKey.SafetyMaxBolus,
            titleResId = app.aaps.core.ui.R.string.max_bolus_title,
            unit = " U"
        )

        // Max carbs
        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.SafetyMaxCarbs,
            titleResId = app.aaps.core.ui.R.string.max_carbs_title,
            unit = " g"
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
