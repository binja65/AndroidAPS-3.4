package app.aaps.plugins.sync.nsclientV3

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.plugins.sync.R

/**
 * Compose implementation of NSClientV3 preferences.
 */
class NSClientV3PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Main NSClient category
        val nsClientSettingsKey = "${keyPrefix}_ns_client_settings"
        item {
            val isExpanded = sectionState?.isExpanded(nsClientSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.ns_client_v3_title,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(nsClientSettingsKey) }
            ) {
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
        }

        // Synchronization category
        val synchronizationKey = "${keyPrefix}_ns_client_synchronization"
        item {
            val isExpanded = sectionState?.isExpanded(synchronizationKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.ns_sync_options,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(synchronizationKey) }
            ) {
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
            }
        }

        // Alarm options category
        val alarmOptionsKey = "${keyPrefix}_ns_client_alarm_options"
        item {
            val isExpanded = sectionState?.isExpanded(alarmOptionsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.ns_alarm_options,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(alarmOptionsKey) }
            ) {
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
            }
        }

        // Connection settings category
        val connectionOptionsKey = "${keyPrefix}_ns_client_connection_options"
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
            }
        }

        // Advanced settings category
        val advancedKey = "${keyPrefix}_ns_client_advanced"
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
        }
    }
}
