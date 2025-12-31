package app.aaps.pump.medtronic

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.pump.medtronic.keys.MedtronicBooleanPreferenceKey
import app.aaps.pump.medtronic.keys.MedtronicIntPreferenceKey
import app.aaps.pump.medtronic.keys.MedtronicStringPreferenceKey

/**
 * Compose implementation of Medtronic preferences.
 * Note: RileyLink-specific preferences are handled separately.
 */
class MedtronicPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    companion object {
        private val bolusDelayEntries = listOf("5", "10", "15")
        private val bolusDelayValues = listOf(5, 10, 15)
    }

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Medtronic pump settings category
        val sectionKey = "${keyPrefix}_medtronic_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.medtronic_name,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
                AdaptiveStringPreferenceItem(
                    preferences = preferences,
                    config = config,
                    stringKey = MedtronicStringPreferenceKey.Serial,
                    titleResId = R.string.medtronic_serial_number
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = MedtronicIntPreferenceKey.MaxBasal,
                    titleResId = R.string.medtronic_pump_max_basal
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = MedtronicIntPreferenceKey.MaxBolus,
                    titleResId = R.string.medtronic_pump_max_bolus
                )

                AdaptiveListIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = MedtronicIntPreferenceKey.BolusDelay,
                    titleResId = R.string.medtronic_pump_bolus_delay,
                    entries = bolusDelayEntries,
                    entryValues = bolusDelayValues
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = MedtronicBooleanPreferenceKey.SetNeutralTemp,
                    titleResId = R.string.set_neutral_temps_title
                )
            }
        }
    }
}
