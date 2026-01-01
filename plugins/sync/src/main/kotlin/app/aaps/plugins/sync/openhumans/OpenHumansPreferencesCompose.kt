package app.aaps.plugins.sync.openhumans

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.sync.R

/**
 * Compose implementation of Open Humans preferences.
 */
class OpenHumansPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.open_humans

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
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

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
