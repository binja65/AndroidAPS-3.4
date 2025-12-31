package app.aaps.pump.diaconn

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.pump.diaconn.keys.DiaconnBooleanKey
import app.aaps.pump.diaconn.keys.DiaconnIntKey

/**
 * Compose implementation of Diaconn G8 preferences.
 */
class DiaconnG8PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    companion object {
        private val speedEntries = listOf("1 U/min", "2 U/min", "3 U/min", "4 U/min", "5 U/min", "6 U/min", "7 U/min", "8 U/min")
        private val speedValues = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    }

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Diaconn G8 pump settings category
        val sectionKey = "${keyPrefix}_diaconn_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.diaconn_g8_pump,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
                AdaptiveListIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = DiaconnIntKey.BolusSpeed,
                    titleResId = R.string.bolusspeed,
                    entries = speedEntries,
                    entryValues = speedValues
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = DiaconnBooleanKey.LogInsulinChange,
                    titleResId = R.string.diaconn_g8_loginsulinchange_title
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = DiaconnBooleanKey.LogCannulaChange,
                    titleResId = R.string.diaconn_g8_logcanulachange_title
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = DiaconnBooleanKey.LogTubeChange,
                    titleResId = R.string.diaconn_g8_logtubechange_title
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = DiaconnBooleanKey.LogBatteryChange,
                    titleResId = R.string.diaconn_g8_logbatterychange_title
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = DiaconnBooleanKey.SendLogsToCloud,
                    titleResId = R.string.diaconn_g8_cloudsend_title
                )
            }
        }
    }
}
