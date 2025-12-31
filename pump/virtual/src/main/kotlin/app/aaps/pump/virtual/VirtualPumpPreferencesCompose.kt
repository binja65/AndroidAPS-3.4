package app.aaps.pump.virtual

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.data.pump.defs.PumpType
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState

/**
 * Compose implementation of Virtual Pump preferences.
 */
class VirtualPumpPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    // Build pump type entries dynamically
    private val pumpTypeEntries = PumpType.entries
        .filter { it.description != "USER" }
        .sortedBy { it.description }
        .associate { it.description to it.description }

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Virtual pump settings category
        val sectionKey = "${keyPrefix}_virtual_pump_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.virtualpump_settings,
                summaryItems = listOf(
                    R.string.virtual_pump_type,
                    app.aaps.core.ui.R.string.virtualpump_uploadstatus_title
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
                AdaptiveStringListPreferenceItem(
                    preferences = preferences,
                    config = config,
                    stringKey = StringKey.VirtualPumpType,
                    titleResId = R.string.virtual_pump_type,
                    entries = pumpTypeEntries
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.VirtualPumpStatusUpload,
                    titleResId = app.aaps.core.ui.R.string.virtualpump_uploadstatus_title
                )
            }
        }
    }
}
