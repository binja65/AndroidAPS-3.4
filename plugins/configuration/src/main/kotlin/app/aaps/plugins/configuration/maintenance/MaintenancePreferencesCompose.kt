package app.aaps.plugins.configuration.maintenance

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.plugins.configuration.R

/**
 * Compose implementation of Maintenance preferences.
 */
class MaintenancePreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Maintenance settings category
        val sectionKey1 = "${keyPrefix}_maintenance_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey1) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.maintenance_settings,
                summaryItems = listOf(
                    R.string.maintenance_email,
                    R.string.maintenance_amount
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey1) }
            ) {
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
        }

        // Data choices category
        val sectionKey2 = "${keyPrefix}_data_choice_setting"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey2) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.data_choices,
                summaryItems = listOf(
                    R.string.fabric_upload,
                    R.string.identification
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey2) }
            ) {
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
            }
        }

        // Unattended export category
        val sectionKey3 = "${keyPrefix}_unattended_export_setting"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey3) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.unattended_settings_export,
                summaryItems = listOf(
                    R.string.unattended_settings_export
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey3) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.MaintenanceEnableExportSettingsAutomation,
                    titleResId = R.string.unattended_settings_export,
                    summaryResId = R.string.unattended_settings_export_summary
                )
            }
        }
    }
}
