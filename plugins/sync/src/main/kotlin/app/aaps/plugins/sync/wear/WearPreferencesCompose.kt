package app.aaps.plugins.sync.wear

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.plugins.sync.R

/**
 * Compose implementation of Wear preferences using adaptive preferences.
 */
class WearPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val showBroadcastOption: Boolean
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Wear settings category
        val wearSettingsKey = "${keyPrefix}_wear_settings"
        item {
            val isExpanded = sectionState?.isExpanded(wearSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.wear_settings,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(wearSettingsKey) }
            ) {
                // Wear control - using adaptive preference with visibility logic
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.WearControl,
                    titleResId = R.string.wearcontrol_title,
                    summaryResId = R.string.wearcontrol_summary
                )

                // Broadcast data (only for AAPSCLIENT)
                if (showBroadcastOption) {
                    AdaptiveSwitchPreferenceItem(
                        preferences = preferences,
                        config = config,
                        booleanKey = BooleanKey.WearBroadcastData,
                        titleResId = R.string.wear_broadcast_data,
                        summaryResId = R.string.wear_broadcast_data_summary
                    )
                }
            }
        }

        // Wizard settings category
        val wizardSettingsKey = "${keyPrefix}_wear_wizard_settings"
        item {
            val isExpanded = sectionState?.isExpanded(wizardSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.wear_wizard_settings,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(wizardSettingsKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.WearWizardBg,
                    titleResId = app.aaps.core.ui.R.string.bg_label
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.WearWizardTt,
                    titleResId = app.aaps.core.ui.R.string.tt_label
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.WearWizardTrend,
                    titleResId = app.aaps.core.ui.R.string.bg_trend_label
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.WearWizardCob,
                    titleResId = app.aaps.core.ui.R.string.treatments_wizard_cob_label
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.WearWizardIob,
                    titleResId = app.aaps.core.ui.R.string.iob_label
                )
            }
        }

        // Custom watchface settings category
        val customWatchfaceKey = "${keyPrefix}_wear_custom_watchface_settings"
        item {
            val isExpanded = sectionState?.isExpanded(customWatchfaceKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.wear_custom_watchface_settings,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(customWatchfaceKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.WearCustomWatchfaceAuthorization,
                    titleResId = R.string.wear_custom_watchface_authorization_title,
                    summaryResId = R.string.wear_custom_watchface_authorization_summary
                )
            }
        }

        // General settings category
        val generalSettingsKey = "${keyPrefix}_wear_general_settings"
        item {
            val isExpanded = sectionState?.isExpanded(generalSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.wear_general_settings,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(generalSettingsKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.WearNotifyOnSmb,
                    titleResId = R.string.wear_notifysmb_title,
                    summaryResId = R.string.wear_notifysmb_summary
                )
            }
        }
    }
}
