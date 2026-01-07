package app.aaps.plugins.sync.nsclient

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceListForListKeys
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.navigable.PreferenceSubScreen
import app.aaps.plugins.sync.R

/**
 * Compose implementation of NSClient preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class NSClientPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.ns_client_title

    override val mainKeys: List<PreferenceKey> = listOf(
        StringKey.NsClientUrl,
        StringKey.NsClientApiSecret
    )

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

    private val alarmKeys: List<PreferenceKey> = listOf(
        BooleanKey.NsClientNotificationsFromAlarms,
        BooleanKey.NsClientNotificationsFromAnnouncements,
        IntKey.NsClientAlarmStaleData,
        IntKey.NsClientUrgentAlarmStaleData
    )

    private val connectionKeys: List<PreferenceKey> = listOf(
        BooleanKey.NsClientUseCellular,
        BooleanKey.NsClientUseRoaming,
        BooleanKey.NsClientUseWifi,
        StringKey.NsClientWifiSsids,
        BooleanKey.NsClientUseOnBattery,
        BooleanKey.NsClientUseOnCharging
    )

    private val advancedKeys: List<PreferenceKey> = listOf(
        BooleanKey.NsClientLogAppStart,
        BooleanKey.NsClientCreateAnnouncementsFromErrors,
        BooleanKey.NsClientCreateAnnouncementsFromCarbsReq,
        BooleanKey.NsClientSlowSync
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceListForListKeys(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        PreferenceSubScreen(
            key = "ns_client_synchronization",
            titleResId = R.string.ns_sync_options,
            keys = syncKeys
        ) { _ ->
            AdaptivePreferenceListForListKeys(
                keys = syncKeys,
                preferences = preferences,
                config = config
            )
        },

        PreferenceSubScreen(
            key = "ns_client_alarm_options",
            titleResId = R.string.ns_alarm_options,
            keys = alarmKeys
        ) { _ ->
            AdaptivePreferenceListForListKeys(
                keys = alarmKeys,
                preferences = preferences,
                config = config
            )
        },

        PreferenceSubScreen(
            key = "ns_client_connection_options",
            titleResId = R.string.connection_settings_title,
            keys = connectionKeys
        ) { _ ->
            AdaptivePreferenceListForListKeys(
                keys = connectionKeys,
                preferences = preferences,
                config = config
            )
        },

        PreferenceSubScreen(
            key = "ns_client_advanced",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            keys = advancedKeys
        ) { _ ->
            AdaptivePreferenceListForListKeys(
                keys = advancedKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
