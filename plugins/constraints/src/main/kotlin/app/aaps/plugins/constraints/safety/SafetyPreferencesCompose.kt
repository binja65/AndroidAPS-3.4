package app.aaps.plugins.constraints.safety

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.utils.HardLimits
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveDoublePreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.plugins.constraints.R

/**
 * Compose implementation of Safety preferences using adaptive preferences.
 */
class SafetyPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val hardLimits: HardLimits
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Safety category
        val sectionKey = "${keyPrefix}_safety_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.treatmentssafety_title,
                summaryItems = listOf(
                    app.aaps.core.ui.R.string.patient_type,
                    app.aaps.core.ui.R.string.max_bolus_title,
                    app.aaps.core.ui.R.string.max_carbs_title
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
                // Patient age preference
                val ageEntries = hardLimits.ageEntryValues().zip(hardLimits.ageEntries()).associate {
                    it.first.toString() to it.second.toString()
                }
                AdaptiveStringListPreferenceItem(
                    preferences = preferences,
                    config = config,
                    stringKey = StringKey.SafetyAge,
                    titleResId = app.aaps.core.ui.R.string.patient_type,
                    entries = ageEntries
                )

                // Max bolus - using adaptive preference with validation
                AdaptiveDoublePreferenceItem(
                    preferences = preferences,
                    config = config,
                    doubleKey = DoubleKey.SafetyMaxBolus,
                    titleResId = app.aaps.core.ui.R.string.max_bolus_title,
                    unit = " U"
                )

                // Max carbs - using adaptive preference with validation
                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = IntKey.SafetyMaxCarbs,
                    titleResId = app.aaps.core.ui.R.string.max_carbs_title,
                    unit = " g"
                )
            }
        }
    }
}
