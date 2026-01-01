package app.aaps.plugins.sync.nsclientV3

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.sync.R

/**
 * Compose implementation of NSClientV3 preferences using navigable subscreens.
 */
class NSClientV3PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.ns_client_v3_title

    // Main content shown at top level (URL, token, websocket)
    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptiveStringPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.NsClientUrl,
            titleResId = R.string.ns_client_url_title,
            summaryResId = R.string.ns_client_url_dialog_message
        )
        AdaptiveStringPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.NsClientAccessToken,
            titleResId = R.string.nsclient_token_title,
            summaryResId = R.string.nsclient_token_dialog_message,
            isPassword = true
        )
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.NsClient3UseWs,
            titleResId = R.string.ns_use_ws_title,
            summaryResId = R.string.ns_use_ws_summary
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Synchronization subscreen
        PreferenceSubScreen(
            key = "ns_client_synchronization",
            titleResId = R.string.ns_sync_options,
            summaryItems = listOf(
                R.string.ns_upload,
                R.string.ns_receive_cgm,
                R.string.ns_receive_profile_store
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientUploadData,
                titleResId = R.string.ns_upload,
                summaryResId = R.string.ns_upload_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.BgSourceUploadToNs,
                titleResId = app.aaps.core.ui.R.string.do_ns_upload_title
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientAcceptCgmData,
                titleResId = R.string.ns_receive_cgm,
                summaryResId = R.string.ns_receive_cgm_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientAcceptProfileStore,
                titleResId = R.string.ns_receive_profile_store,
                summaryResId = R.string.ns_receive_profile_store_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientAcceptTempTarget,
                titleResId = R.string.ns_receive_temp_target,
                summaryResId = R.string.ns_receive_temp_target_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientAcceptProfileSwitch,
                titleResId = R.string.ns_receive_profile_switch,
                summaryResId = R.string.ns_receive_profile_switch_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientAcceptInsulin,
                titleResId = R.string.ns_receive_insulin,
                summaryResId = R.string.ns_receive_insulin_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientAcceptCarbs,
                titleResId = R.string.ns_receive_carbs,
                summaryResId = R.string.ns_receive_carbs_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientAcceptTherapyEvent,
                titleResId = R.string.ns_receive_therapy_events,
                summaryResId = R.string.ns_receive_therapy_events_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientAcceptRunningMode,
                titleResId = R.string.ns_receive_running_mode,
                summaryResId = R.string.ns_receive_running_mode_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientAcceptTbrEb,
                titleResId = R.string.ns_receive_tbr_eb,
                summaryResId = R.string.ns_receive_tbr_eb_summary
            )
        },

        // Alarm options subscreen
        PreferenceSubScreen(
            key = "ns_client_alarm_options",
            titleResId = R.string.ns_alarm_options,
            summaryItems = listOf(
                R.string.ns_alarms,
                R.string.ns_announcements,
                R.string.ns_alarm_stale_data_value_label
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientNotificationsFromAlarms,
                titleResId = R.string.ns_alarms
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientNotificationsFromAnnouncements,
                titleResId = R.string.ns_announcements
            )
            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.NsClientAlarmStaleData,
                titleResId = R.string.ns_alarm_stale_data_value_label
            )
            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.NsClientUrgentAlarmStaleData,
                titleResId = R.string.ns_alarm_urgent_stale_data_value_label
            )
        },

        // Connection settings subscreen
        PreferenceSubScreen(
            key = "ns_client_connection_options",
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
            key = "ns_client_advanced",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            summaryItems = listOf(
                R.string.ns_log_app_started_event,
                R.string.ns_create_announcements_from_errors_title,
                R.string.ns_sync_slow
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientLogAppStart,
                titleResId = R.string.ns_log_app_started_event
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientCreateAnnouncementsFromErrors,
                titleResId = R.string.ns_create_announcements_from_errors_title,
                summaryResId = R.string.ns_create_announcements_from_errors_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientCreateAnnouncementsFromCarbsReq,
                titleResId = R.string.ns_create_announcements_from_carbs_req_title,
                summaryResId = R.string.ns_create_announcements_from_carbs_req_summary
            )
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.NsClientSlowSync,
                titleResId = R.string.ns_sync_slow
            )
        }
    )
}
