package app.aaps.plugins.sync.xdrip

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntentKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntentPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.sync.R

/**
 * Compose implementation of xDrip preferences.
 */
class XdripPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.xdrip

    // Main content shown at top level
    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // xDrip local broadcasts info (read-only)
        AdaptiveIntentPreferenceItem(
            preferences = preferences,
            intentKey = IntentKey.XdripInfo,
            titleResId = R.string.xdrip_local_broadcasts_title,
            summaryResId = R.string.xdrip_local_broadcasts_summary,
            onClick = { /* Informational only - no action */ }
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.XdripSendStatus,
            titleResId = R.string.xdrip_send_status_title
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // xDrip status settings subscreen
        PreferenceSubScreen(
            key = "xdrip_advanced",
            titleResId = R.string.xdrip_status_settings,
            summaryItems = listOf(
                R.string.xdrip_status_detailed_iob_title,
                R.string.xdrip_status_show_bgi_title
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.XdripSendDetailedIob,
                titleResId = R.string.xdrip_status_detailed_iob_title,
                summaryResId = R.string.xdrip_status_detailed_iob_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.XdripSendBgi,
                titleResId = R.string.xdrip_status_show_bgi_title,
                summaryResId = R.string.xdrip_status_show_bgi_summary
            )
        }
    )
}
