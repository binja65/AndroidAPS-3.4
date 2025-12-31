package info.nightscout.pump.combov2

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveActivityPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntentPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import info.nightscout.pump.combov2.activities.ComboV2PairingActivity
import info.nightscout.pump.combov2.keys.ComboBooleanKey
import info.nightscout.pump.combov2.keys.ComboIntentKey
import info.nightscout.pump.combov2.keys.ComboIntKey

/**
 * Compose implementation of ComboV2 preferences.
 */
class ComboV2PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val onUnpairClick: () -> Unit = {}
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // ComboV2 pump settings category
        val sectionKey = "${keyPrefix}_combov2_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.combov2_title,
                summaryItems = listOf(
                    R.string.combov2_pair_with_pump_title,
                    R.string.combov2_discovery_duration,
                    R.string.combov2_automatic_reservoir_entry,
                    R.string.combov2_automatic_battery_entry
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
                AdaptiveActivityPreferenceItem(
                    preferences = preferences,
                    intentKey = ComboIntentKey.PairWithPump,
                    titleResId = R.string.combov2_pair_with_pump_title,
                    activityClass = ComboV2PairingActivity::class.java,
                    summaryResId = R.string.combov2_pair_with_pump_summary
                )

                AdaptiveIntentPreferenceItem(
                    preferences = preferences,
                    intentKey = ComboIntentKey.UnpairPump,
                    titleResId = R.string.combov2_unpair_pump_title,
                    summaryResId = R.string.combov2_unpair_pump_summary,
                    onClick = onUnpairClick
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = ComboIntKey.DiscoveryDuration,
                    titleResId = R.string.combov2_discovery_duration
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = ComboBooleanKey.AutomaticReservoirEntry,
                    titleResId = R.string.combov2_automatic_reservoir_entry
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = ComboBooleanKey.AutomaticBatteryEntry,
                    titleResId = R.string.combov2_automatic_battery_entry
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = ComboBooleanKey.VerboseLogging,
                    titleResId = R.string.combov2_verbose_logging
                )
            }
        }
    }
}
