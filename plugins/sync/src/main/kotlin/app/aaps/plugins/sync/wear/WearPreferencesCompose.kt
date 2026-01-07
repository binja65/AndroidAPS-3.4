package app.aaps.plugins.sync.wear

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceListForListKeys
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.navigable.PreferenceSubScreen
import app.aaps.plugins.sync.R

/**
 * Compose implementation of Wear preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class WearPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.core.ui.R.string.wear

    // WearBroadcastData has showInApsMode=false, showInPumpControlMode=false
    // so it only appears in NSClient mode (handled by AdaptivePreferenceList)
    override val mainKeys: List<PreferenceKey> = listOf(
        BooleanKey.WearControl,
        BooleanKey.WearBroadcastData
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
        AdaptivePreferenceListForListKeys(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        PreferenceSubScreen(
            key = "wear_wizard_settings",
            titleResId = app.aaps.core.ui.R.string.wear_wizard_settings,
            keys = wizardKeys,
            summaryResId = R.string.wear_wizard_settings_summary
        ) { _ ->
            AdaptivePreferenceListForListKeys(
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
            AdaptivePreferenceListForListKeys(
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
            AdaptivePreferenceListForListKeys(
                keys = generalKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
