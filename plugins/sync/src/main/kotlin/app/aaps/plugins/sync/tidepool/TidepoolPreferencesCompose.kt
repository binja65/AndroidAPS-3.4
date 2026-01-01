package app.aaps.plugins.sync.tidepool

import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.sync.R
import app.aaps.plugins.sync.tidepool.keys.TidepoolBooleanKey

/**
 * Compose implementation of Tidepool preferences.
 */
class TidepoolPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.tidepool

    // No main content - Tidepool only has subscreens
    override val mainContent = null

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Connection options subscreen
        PreferenceSubScreen(
            key = "tidepool_connection_options",
            titleResId = R.string.connection_settings_title,
            summaryItems = listOf(
                R.string.ns_cellular,
                R.string.ns_wifi,
                R.string.ns_wifi_ssids
            )
        ) { _ ->
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
                titleResId = R.string.ns_wifi_ssids,
                summaryResId = R.string.ns_wifi_allowed_ssids
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
        },

        // Advanced settings subscreen
        PreferenceSubScreen(
            key = "tidepool_advanced",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            summaryItems = listOf(
                R.string.title_tidepool_dev_servers
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = TidepoolBooleanKey.UseTestServers,
                titleResId = R.string.title_tidepool_dev_servers,
                summaryResId = R.string.summary_tidepool_dev_servers
            )
        }
    )
}
