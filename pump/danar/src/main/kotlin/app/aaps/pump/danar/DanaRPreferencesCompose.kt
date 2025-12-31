package app.aaps.pump.danar

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.pump.dana.R
import app.aaps.pump.dana.keys.DanaBooleanKey
import app.aaps.pump.dana.keys.DanaIntKey

/**
 * Compose implementation of DanaR preferences.
 * Note: Bluetooth device selection is handled separately as it requires runtime permissions.
 */
class DanaRPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // DanaR pump settings category
        val sectionKey = "${keyPrefix}_danar_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.danar_pump_settings,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = DanaIntKey.Password,
                    titleResId = R.string.danar_password_title
                )

                AdaptiveListIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = DanaIntKey.BolusSpeed,
                    titleResId = R.string.bolusspeed,
                    entries = listOf("12 s/U", "30 s/U", "60 s/U"),
                    entryValues = listOf(0, 1, 2)
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = DanaBooleanKey.UseExtended,
                    titleResId = R.string.danar_useextended_title
                )
            }
        }
    }
}
