package app.aaps.plugins.sync.garmin

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceListForListKeys
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.navigable.PreferenceSubScreen
import app.aaps.plugins.sync.R
import app.aaps.plugins.sync.garmin.keys.GarminBooleanKey
import app.aaps.plugins.sync.garmin.keys.GarminIntKey
import app.aaps.plugins.sync.garmin.keys.GarminStringKey

/**
 * Compose implementation of Garmin preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class GarminPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.garmin

    override val mainKeys: List<PreferenceKey> = listOf(
        GarminBooleanKey.LocalHttpServer,
        GarminIntKey.LocalHttpPort,
        GarminStringKey.RequestKey
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceListForListKeys(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
