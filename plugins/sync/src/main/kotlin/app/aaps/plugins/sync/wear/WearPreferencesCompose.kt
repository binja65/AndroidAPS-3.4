package app.aaps.plugins.sync.wear

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
 * Compose implementation of Wear preferences using adaptive preferences.
 */
class WearPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val showBroadcastOption: Boolean
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.core.ui.R.string.wear

    // Main content shown at top level
    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Wear control
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

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Wizard settings subscreen
        PreferenceSubScreen(
            key = "wear_wizard_settings",
            titleResId = app.aaps.core.ui.R.string.wear_wizard_settings,
            summaryResId = R.string.wear_wizard_settings_summary,
            summaryItems = listOf(
                app.aaps.core.ui.R.string.bg_label,
                app.aaps.core.ui.R.string.tt_label,
                app.aaps.core.ui.R.string.treatments_wizard_cob_label,
                app.aaps.core.ui.R.string.iob_label
            )
        ) { _ ->
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
        },

        // Custom watchface settings subscreen
        PreferenceSubScreen(
            key = "wear_custom_watchface_settings",
            titleResId = R.string.wear_custom_watchface_settings,
            summaryItems = listOf(
                R.string.wear_custom_watchface_authorization_title
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.WearCustomWatchfaceAuthorization,
                titleResId = R.string.wear_custom_watchface_authorization_title,
                summaryResId = R.string.wear_custom_watchface_authorization_summary
            )
        },

        // General settings subscreen
        PreferenceSubScreen(
            key = "wear_general_settings",
            titleResId = R.string.wear_general_settings,
            summaryItems = listOf(
                R.string.wear_notifysmb_title
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.WearNotifyOnSmb,
                titleResId = R.string.wear_notifysmb_title,
                summaryResId = R.string.wear_notifysmb_summary
            )
        }
    )
}
