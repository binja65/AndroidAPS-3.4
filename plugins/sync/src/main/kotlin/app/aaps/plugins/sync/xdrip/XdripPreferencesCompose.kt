package app.aaps.plugins.sync.xdrip

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntentKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntentPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.plugins.sync.R

/**
 * Compose implementation of xDrip preferences using elevated cards.
 */
class XdripPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // xDrip settings category
        val xdripSettingsKey = "${keyPrefix}_xdrip_settings"
        item {
            val isExpanded = sectionState?.isExpanded(xdripSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.xdrip,
                summaryItems = listOf(
                    R.string.xdrip_local_broadcasts_title,
                    R.string.xdrip_send_status_title
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(xdripSettingsKey) }
            ) {
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
        }

        // xDrip status settings category
        val xdripAdvancedKey = "${keyPrefix}_xdrip_advanced"
        item {
            val isExpanded = sectionState?.isExpanded(xdripAdvancedKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.xdrip_status_settings,
                summaryItems = listOf(
                    R.string.xdrip_status_detailed_iob_title,
                    R.string.xdrip_status_show_bgi_title
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(xdripAdvancedKey) }
            ) {
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
        }
    }
}
