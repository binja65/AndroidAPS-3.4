package app.aaps.pump.medtronic

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveActivityPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.common.dialog.RileyLinkBLEConfigActivity
import app.aaps.pump.common.hw.rileylink.ble.defs.RileyLinkEncodingType
import app.aaps.pump.common.hw.rileylink.ble.defs.RileyLinkTargetFrequency
import app.aaps.pump.common.hw.rileylink.keys.RileyLinkIntentPreferenceKey
import app.aaps.pump.common.hw.rileylink.keys.RileyLinkStringPreferenceKey
import app.aaps.pump.common.hw.rileylink.keys.RileylinkBooleanPreferenceKey
import app.aaps.pump.medtronic.defs.BatteryType
import app.aaps.pump.medtronic.keys.MedtronicBooleanPreferenceKey
import app.aaps.pump.medtronic.keys.MedtronicIntPreferenceKey
import app.aaps.pump.medtronic.keys.MedtronicStringPreferenceKey

/**
 * Compose implementation of Medtronic preferences.
 */
class MedtronicPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    companion object {

        private val bolusDelayEntries = listOf("5", "10", "15")
        private val bolusDelayValues = listOf(5, 10, 15)

        // Pump type entries - stored value maps to display text
        private val pumpTypeEntries = mapOf(
            "Other (unsupported)" to "Other (unsupported)",
            "512" to "512",
            "712" to "712",
            "515" to "515",
            "715" to "715",
            "522" to "522",
            "722" to "722",
            "523 (Fw 2.4A or lower)" to "523 (Fw 2.4A or lower)",
            "723 (Fw 2.4A or lower)" to "723 (Fw 2.4A or lower)",
            "554 (EU Fw. <= 2.6A)" to "554 (EU Fw. <= 2.6A)",
            "754 (EU Fw. <= 2.6A)" to "754 (EU Fw. <= 2.6A)",
            "554 (CA Fw. <= 2.7A)" to "554 (CA Fw. <= 2.7A)",
            "754 (CA Fw. <= 2.7A)" to "754 (CA Fw. <= 2.7A)"
        )

        // Pump frequency entries - storage key maps to display text
        private val pumpFrequencyEntries = mapOf(
            RileyLinkTargetFrequency.MedtronicUS.key!! to "US & Canada (916 MHz)",
            RileyLinkTargetFrequency.MedtronicWorldWide.key!! to "Worldwide (868 MHz)"
        )

        // Encoding entries - storage key maps to display text
        private val encodingEntries = mapOf(
            RileyLinkEncodingType.FourByteSixByteLocal.key!! to "Software (4b6b) Local",
            RileyLinkEncodingType.FourByteSixByteRileyLink.key!! to "Hardware (4b6b) RileyLink"
        )

        // Battery type entries - storage key maps to display text
        private val batteryTypeEntries = BatteryType.entries.associate { it.key to it.key }
    }

    override val titleResId: Int = R.string.medtronic_name

    override val summaryItems: List<Int> = listOf(
        R.string.medtronic_serial_number,
        R.string.medtronic_pump_type,
        R.string.medtronic_pump_frequency
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptiveStringPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = MedtronicStringPreferenceKey.Serial,
            titleResId = R.string.medtronic_serial_number
        )

        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = MedtronicStringPreferenceKey.PumpType,
            titleResId = R.string.medtronic_pump_type,
            entries = pumpTypeEntries
        )

        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = MedtronicStringPreferenceKey.PumpFrequency,
            titleResId = R.string.medtronic_pump_frequency,
            entries = pumpFrequencyEntries
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

        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = RileyLinkStringPreferenceKey.Encoding,
            titleResId = R.string.medtronic_pump_encoding,
            entries = encodingEntries
        )

        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = MedtronicStringPreferenceKey.BatteryType,
            titleResId = R.string.medtronic_pump_battery_select,
            entries = batteryTypeEntries
        )

        AdaptiveActivityPreferenceItem(
            preferences = preferences,
            intentKey = RileyLinkIntentPreferenceKey.MacAddressSelector,
            titleResId = app.aaps.pump.common.hw.rileylink.R.string.rileylink_configuration,
            activityClass = RileyLinkBLEConfigActivity::class.java
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = RileylinkBooleanPreferenceKey.OrangeUseScanning,
            titleResId = app.aaps.pump.common.hw.rileylink.R.string.orange_use_scanning_level,
            summaryResId = app.aaps.pump.common.hw.rileylink.R.string.orange_use_scanning_level_summary
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = RileylinkBooleanPreferenceKey.ShowReportedBatteryLevel,
            titleResId = app.aaps.pump.common.hw.rileylink.R.string.riley_link_show_battery_level,
            summaryResId = app.aaps.pump.common.hw.rileylink.R.string.riley_link_show_battery_level_summary
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = MedtronicBooleanPreferenceKey.SetNeutralTemp,
            titleResId = R.string.set_neutral_temps_title,
            summaryResId = R.string.set_neutral_temps_summary
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
