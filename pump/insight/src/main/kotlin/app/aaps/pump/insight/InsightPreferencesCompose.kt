package app.aaps.pump.insight

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.pump.insight.keys.InsightBooleanKey
import app.aaps.pump.insight.keys.InsightIntKey

/**
 * Compose implementation of Insight preferences.
 */
class InsightPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Insight pump settings category
        val sectionKey = "${keyPrefix}_insight_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.insight_local,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = InsightBooleanKey.LogReservoirChanges,
                    titleResId = R.string.log_reservoir_changes
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = InsightBooleanKey.LogTubeChanges,
                    titleResId = R.string.log_tube_changes
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = InsightBooleanKey.LogSiteChanges,
                    titleResId = R.string.log_site_changes
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = InsightBooleanKey.LogBatteryChanges,
                    titleResId = R.string.log_battery_changes
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = InsightBooleanKey.EnableTbrEmulation,
                    titleResId = R.string.enable_tbr_emulation
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = InsightIntKey.MinRecoveryDuration,
                    titleResId = R.string.min_recovery_duration
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = InsightIntKey.MaxRecoveryDuration,
                    titleResId = R.string.max_recovery_duration
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = InsightIntKey.DisconnectDelay,
                    titleResId = R.string.disconnect_delay
                )
            }
        }
    }
}
