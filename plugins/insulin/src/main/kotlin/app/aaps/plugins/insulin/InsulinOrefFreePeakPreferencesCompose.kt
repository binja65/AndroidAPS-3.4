package app.aaps.plugins.insulin

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceListForListKeys
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.navigable.PreferenceSubScreen

/**
 * Compose implementation of Insulin Free Peak preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class InsulinOrefFreePeakPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.free_peak_oref

    override val mainKeys: List<PreferenceKey> = listOf(
        IntKey.InsulinOrefPeak
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
