package app.aaps.pump.equil

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
import app.aaps.pump.equil.keys.EquilBooleanPreferenceKey
import app.aaps.pump.equil.keys.EquilIntPreferenceKey

/**
 * Compose implementation of Equil preferences.
 */
class EquilPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.equil_name

    override val mainKeys: List<PreferenceKey> = listOf(
        EquilBooleanPreferenceKey.EquilAlarmBattery,
        EquilBooleanPreferenceKey.EquilAlarmInsulin,
        EquilIntPreferenceKey.EquilTone
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // All preferences are key-based (EquilTone has entries on key)
        AdaptivePreferenceListForListKeys(
            keys = listOf(
                EquilBooleanPreferenceKey.EquilAlarmBattery,
                EquilBooleanPreferenceKey.EquilAlarmInsulin,
                EquilIntPreferenceKey.EquilTone
            ),
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
