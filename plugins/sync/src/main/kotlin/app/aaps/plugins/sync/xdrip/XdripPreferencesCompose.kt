package app.aaps.plugins.sync.xdrip

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.sync.R
import app.aaps.plugins.sync.xdrip.keys.XdripIntentKey

/**
 * Compose implementation of xDrip preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class XdripPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.xdrip

    override val mainKeys: List<PreferenceKey> = listOf(
        XdripIntentKey.Info,
        BooleanKey.XdripSendStatus
    )

    private val advancedKeys: List<PreferenceKey> = listOf(
        BooleanKey.XdripSendDetailedIob,
        BooleanKey.XdripSendBgi
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        PreferenceSubScreen(
            key = "xdrip_advanced",
            titleResId = R.string.xdrip_status_settings,
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
