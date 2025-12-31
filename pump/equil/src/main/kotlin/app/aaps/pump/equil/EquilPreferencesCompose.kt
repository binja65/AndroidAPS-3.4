package app.aaps.pump.equil

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.pump.equil.keys.EquilBooleanPreferenceKey
import app.aaps.pump.equil.keys.EquilIntPreferenceKey

/**
 * Compose implementation of Equil preferences.
 */
class EquilPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val rh: ResourceHelper
) : PreferenceScreenContent {

    private val toneEntries = listOf(
        rh.gs(R.string.equil_tone_mode_mute),
        rh.gs(R.string.equil_tone_mode_tone),
        rh.gs(R.string.equil_tone_mode_shake),
        rh.gs(R.string.equil_tone_mode_tone_and_shake)
    )
    private val toneValues = listOf(0, 1, 2, 3)

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Equil pump settings category
        val sectionKey = "${keyPrefix}_equil_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.equil_name,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
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
        }
    }
}
