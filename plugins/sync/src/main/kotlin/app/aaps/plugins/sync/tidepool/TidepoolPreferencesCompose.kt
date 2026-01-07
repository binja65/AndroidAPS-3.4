package app.aaps.plugins.sync.tidepool

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceListForListKeys
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.navigable.PreferenceSubScreen
import app.aaps.plugins.sync.R
import app.aaps.plugins.sync.tidepool.keys.TidepoolBooleanKey

/**
 * Compose implementation of Tidepool preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class TidepoolPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.tidepool

    private val connectionKeys: List<PreferenceKey> = listOf(
        BooleanKey.NsClientUseCellular,
        BooleanKey.NsClientUseRoaming,
        BooleanKey.NsClientUseWifi,
        StringKey.NsClientWifiSsids,
        BooleanKey.NsClientUseOnBattery,
        BooleanKey.NsClientUseOnCharging
    )

    private val advancedKeys: List<PreferenceKey> = listOf(
        TidepoolBooleanKey.UseTestServers
    )

    override val mainContent = null

    override val subscreens: List<PreferenceSubScreen> = listOf(
        PreferenceSubScreen(
            key = "tidepool_connection_options",
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
            key = "tidepool_advanced",
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
