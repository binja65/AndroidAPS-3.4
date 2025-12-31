package app.aaps.plugins.automation

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.sharedPreferences.SP
import app.aaps.core.keys.StringKey
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.StringListPreferenceItem

/**
 * Compose implementation of Automation preferences.
 */
class AutomationPreferencesCompose(
    private val sp: SP
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Automation category
        val automationSettingsKey = "${keyPrefix}_automation_settings"
        item {
            val isExpanded = sectionState?.isExpanded(automationSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.automation,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(automationSettingsKey) }
            ) {
                // Location service preference
                StringListPreferenceItem(
                    sp = sp,
                    key = StringKey.AutomationLocation.key,
                    defaultValue = StringKey.AutomationLocation.defaultValue,
                    entries = mapOf(
                        "PASSIVE" to R.string.use_passive_location,
                        "NETWORK" to R.string.use_network_location,
                        "GPS" to R.string.use_gps_location
                    ),
                    titleResId = R.string.locationservice
                )
            }
        }
    }
}
