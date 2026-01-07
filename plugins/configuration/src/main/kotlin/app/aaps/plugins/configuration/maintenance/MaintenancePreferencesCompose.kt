package app.aaps.plugins.configuration.maintenance

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
import app.aaps.plugins.configuration.R

/**
 * Compose implementation of Maintenance preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class MaintenancePreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.maintenance

    override val mainKeys: List<PreferenceKey> = listOf(
        StringKey.MaintenanceEmail,
        IntKey.MaintenanceLogsAmount
    )

    private val dataChoiceKeys: List<PreferenceKey> = listOf(
        BooleanKey.MaintenanceEnableFabric,
        StringKey.MaintenanceIdentification
    )

    private val unattendedExportKeys: List<PreferenceKey> = listOf(
        BooleanKey.MaintenanceEnableExportSettingsAutomation
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
            key = "data_choice_setting",
            titleResId = R.string.data_choices,
            keys = dataChoiceKeys
        ) { _ ->
            AdaptivePreferenceListForListKeys(
                keys = dataChoiceKeys,
                preferences = preferences,
                config = config
            )
        },

        PreferenceSubScreen(
            key = "unattended_export_setting",
            titleResId = R.string.unattended_settings_export,
            keys = unattendedExportKeys
        ) { _ ->
            AdaptivePreferenceListForListKeys(
                keys = unattendedExportKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
