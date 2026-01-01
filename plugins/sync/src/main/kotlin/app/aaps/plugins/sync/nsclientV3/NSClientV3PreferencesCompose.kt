package app.aaps.plugins.sync.nsclientV3

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.sync.R

/**
 * Compose implementation of NSClientV3 preferences using navigable subscreens.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class NSClientV3PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.ns_client_v3_title

    // Main keys shown at top level
    override val mainKeys: List<PreferenceKey> = listOf(
        StringKey.NsClientUrl,
        StringKey.NsClientAccessToken,
        BooleanKey.NsClient3UseWs
    )

    // Synchronization keys
    private val syncKeys: List<PreferenceKey> = listOf(
        BooleanKey.NsClientUploadData,
        BooleanKey.BgSourceUploadToNs,
        BooleanKey.NsClientAcceptCgmData,
        BooleanKey.NsClientAcceptProfileStore,
        BooleanKey.NsClientAcceptTempTarget,
        BooleanKey.NsClientAcceptProfileSwitch,
        BooleanKey.NsClientAcceptInsulin,
        BooleanKey.NsClientAcceptCarbs,
        BooleanKey.NsClientAcceptTherapyEvent,
        BooleanKey.NsClientAcceptRunningMode,
        BooleanKey.NsClientAcceptTbrEb
    )

    // Alarm keys
    private val alarmKeys: List<PreferenceKey> = listOf(
        BooleanKey.NsClientNotificationsFromAlarms,
        BooleanKey.NsClientNotificationsFromAnnouncements,
        IntKey.NsClientAlarmStaleData,
        IntKey.NsClientUrgentAlarmStaleData
    )

    // Connection keys
    private val connectionKeys: List<PreferenceKey> = listOf(
        BooleanKey.NsClientUseCellular,
        BooleanKey.NsClientUseRoaming,
        BooleanKey.NsClientUseWifi,
        StringKey.NsClientWifiSsids,
        BooleanKey.NsClientUseOnBattery,
        BooleanKey.NsClientUseOnCharging
    )

    // Advanced keys
    private val advancedKeys: List<PreferenceKey> = listOf(
        BooleanKey.NsClientLogAppStart,
        BooleanKey.NsClientCreateAnnouncementsFromErrors,
        BooleanKey.NsClientCreateAnnouncementsFromCarbsReq,
        BooleanKey.NsClientSlowSync
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Synchronization subscreen
        PreferenceSubScreen(
            key = "ns_client_synchronization",
            titleResId = R.string.ns_sync_options,
            keys = syncKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = syncKeys,
                preferences = preferences,
                config = config
            )
        },

        // Alarm options subscreen
        PreferenceSubScreen(
            key = "ns_client_alarm_options",
            titleResId = R.string.ns_alarm_options,
            keys = alarmKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = alarmKeys,
                preferences = preferences,
                config = config
            )
        },

        // Connection settings subscreen
        PreferenceSubScreen(
            key = "ns_client_connection_options",
            titleResId = R.string.connection_settings_title,
            keys = connectionKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = connectionKeys,
                preferences = preferences,
                config = config
            )
        },

        // Advanced settings subscreen
        PreferenceSubScreen(
            key = "ns_client_advanced",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            keys = advancedKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = advancedKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
