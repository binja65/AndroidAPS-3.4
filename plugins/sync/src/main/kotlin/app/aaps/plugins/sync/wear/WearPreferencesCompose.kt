package app.aaps.plugins.sync.wear

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.sync.R

/**
 * Compose implementation of Wear preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class WearPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val showBroadcastOption: Boolean
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.core.ui.R.string.wear

    override val mainKeys: List<PreferenceKey> = listOf(
        BooleanKey.WearControl
    )

    private val wizardKeys: List<PreferenceKey> = listOf(
        BooleanKey.WearWizardBg,
        BooleanKey.WearWizardTt,
        BooleanKey.WearWizardTrend,
        BooleanKey.WearWizardCob,
        BooleanKey.WearWizardIob
    )

    private val customWatchfaceKeys: List<PreferenceKey> = listOf(
        BooleanKey.WearCustomWatchfaceAuthorization
    )

    private val generalKeys: List<PreferenceKey> = listOf(
        BooleanKey.WearNotifyOnSmb
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )

        // Conditional broadcast option (only for AAPSCLIENT)
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
        PreferenceSubScreen(
            key = "wear_wizard_settings",
            titleResId = app.aaps.core.ui.R.string.wear_wizard_settings,
            keys = wizardKeys,
            summaryResId = R.string.wear_wizard_settings_summary
        ) { _ ->
            AdaptivePreferenceList(
                keys = wizardKeys,
                preferences = preferences,
                config = config
            )
        },

        PreferenceSubScreen(
            key = "wear_custom_watchface_settings",
            titleResId = R.string.wear_custom_watchface_settings,
            keys = customWatchfaceKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = customWatchfaceKeys,
                preferences = preferences,
                config = config
            )
        },

        PreferenceSubScreen(
            key = "wear_general_settings",
            titleResId = R.string.wear_general_settings,
            keys = generalKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = generalKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
