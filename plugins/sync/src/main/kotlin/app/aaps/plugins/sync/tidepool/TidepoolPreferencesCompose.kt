package app.aaps.plugins.sync.tidepool

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.plugins.sync.R
import app.aaps.plugins.sync.tidepool.keys.TidepoolBooleanKey

/**
 * Compose implementation of Tidepool preferences.
 */
class TidepoolPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Connection options category
        val connectionOptionsKey = "${keyPrefix}_tidepool_connection_options"
        item {
            val isExpanded = sectionState?.isExpanded(connectionOptionsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.connection_settings_title,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(connectionOptionsKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.NsClientUseCellular,
                    titleResId = R.string.ns_cellular
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.NsClientUseRoaming,
                    titleResId = R.string.ns_allow_roaming
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.NsClientUseWifi,
                    titleResId = R.string.ns_wifi
                )

                AdaptiveStringPreferenceItem(
                    preferences = preferences,
                    config = config,
                    stringKey = StringKey.NsClientWifiSsids,
                    titleResId = R.string.ns_wifi_ssids
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.NsClientUseOnBattery,
                    titleResId = R.string.ns_battery
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.NsClientUseOnCharging,
                    titleResId = R.string.ns_charging
                )
            }
        }

        // Advanced settings category
        val advancedKey = "${keyPrefix}_tidepool_advanced"
        item {
            val isExpanded = sectionState?.isExpanded(advancedKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(advancedKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = TidepoolBooleanKey.UseTestServers,
                    titleResId = R.string.title_tidepool_dev_servers,
                    summaryResId = R.string.summary_tidepool_dev_servers
                )
            }
        }
    }
}
