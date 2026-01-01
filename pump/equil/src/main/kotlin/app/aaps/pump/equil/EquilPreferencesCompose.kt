package app.aaps.pump.equil

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.equil.keys.EquilBooleanPreferenceKey
import app.aaps.pump.equil.keys.EquilIntPreferenceKey

/**
 * Compose implementation of Equil preferences.
 */
class EquilPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val rh: ResourceHelper
) : NavigablePreferenceContent {

    private val toneEntries = listOf(
        rh.gs(R.string.equil_tone_mode_mute),
        rh.gs(R.string.equil_tone_mode_tone),
        rh.gs(R.string.equil_tone_mode_shake),
        rh.gs(R.string.equil_tone_mode_tone_and_shake)
    )
    private val toneValues = listOf(0, 1, 2, 3)

    override val titleResId: Int = R.string.equil_name

    override val summaryItems: List<Int> = listOf(
        R.string.equil_settings_alarm_battery,
        R.string.equil_settings_alarm_insulin,
        R.string.equil_tone
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = EquilBooleanPreferenceKey.EquilAlarmBattery,
            titleResId = R.string.equil_settings_alarm_battery
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = EquilBooleanPreferenceKey.EquilAlarmInsulin,
            titleResId = R.string.equil_settings_alarm_insulin
        )

        AdaptiveListIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = EquilIntPreferenceKey.EquilTone,
            titleResId = R.string.equil_tone,
            entries = toneEntries,
            entryValues = toneValues
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
