package app.aaps.plugins.automation

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceListForListKeys
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.navigable.PreferenceSubScreen

/**
 * Compose implementation of Automation preferences.
 * Now fully key-based - UI is auto-generated from preference keys.
 */
class AutomationPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.core.ui.R.string.automation

    override val mainKeys: List<PreferenceKey> = listOf(
        StringKey.AutomationLocation
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
