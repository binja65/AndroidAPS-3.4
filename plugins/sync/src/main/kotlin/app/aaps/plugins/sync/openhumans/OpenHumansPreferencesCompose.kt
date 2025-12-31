package app.aaps.plugins.sync.openhumans

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.plugins.sync.R

/**
 * Compose implementation of Open Humans preferences.
 */
class OpenHumansPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Open Humans settings category
        val openHumansSettingsKey = "${keyPrefix}_open_humans_settings"
        item {
            val isExpanded = sectionState?.isExpanded(openHumansSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.open_humans,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(openHumansSettingsKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.OpenHumansWifiOnly,
                    titleResId = R.string.only_upload_if_connected_to_wifi
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.OpenHumansChargingOnly,
                    titleResId = R.string.only_upload_if_charging
                )
            }
        }
    }
}
