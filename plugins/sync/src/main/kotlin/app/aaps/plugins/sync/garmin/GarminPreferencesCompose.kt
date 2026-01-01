package app.aaps.plugins.sync.garmin

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.sync.R
import app.aaps.plugins.sync.garmin.keys.GarminBooleanKey
import app.aaps.plugins.sync.garmin.keys.GarminIntKey
import app.aaps.plugins.sync.garmin.keys.GarminStringKey

/**
 * Compose implementation of Garmin preferences.
 */
class GarminPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.garmin

    override val summaryItems: List<Int> = listOf(
        R.string.garmin_local_http_server,
        R.string.garmin_local_http_server_port,
        R.string.garmin_request_key
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = GarminBooleanKey.LocalHttpServer,
            titleResId = R.string.garmin_local_http_server
        )

        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = GarminIntKey.LocalHttpPort,
            titleResId = R.string.garmin_local_http_server_port
        )

        AdaptiveStringPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = GarminStringKey.RequestKey,
            titleResId = R.string.garmin_request_key,
            summaryResId = R.string.garmin_request_key_summary
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
