package app.aaps.plugins.configuration.maintenance

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.configuration.R

/**
 * Compose implementation of Maintenance preferences.
 */
class MaintenancePreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.maintenance

    // Main content shown at top level
    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptiveStringPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.MaintenanceEmail,
            titleResId = R.string.maintenance_email
        )

        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.MaintenanceLogsAmount,
            titleResId = R.string.maintenance_amount
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Data choices subscreen
        PreferenceSubScreen(
            key = "data_choice_setting",
            titleResId = R.string.data_choices,
            summaryItems = listOf(
                R.string.fabric_upload,
                R.string.identification
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.MaintenanceEnableFabric,
                titleResId = R.string.fabric_upload
            )

            AdaptiveStringPreferenceItem(
                preferences = preferences,
                config = config,
                stringKey = StringKey.MaintenanceIdentification,
                titleResId = R.string.identification
            )
        },

        // Unattended export subscreen
        PreferenceSubScreen(
            key = "unattended_export_setting",
            titleResId = R.string.unattended_settings_export,
            summaryItems = listOf(
                R.string.unattended_settings_export
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.MaintenanceEnableExportSettingsAutomation,
                titleResId = R.string.unattended_settings_export,
                summaryResId = R.string.unattended_settings_export_summary
            )
        }
    )
}
