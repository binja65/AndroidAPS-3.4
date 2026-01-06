package app.aaps.pump.medtronic

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.common.hw.rileylink.keys.RileyLinkIntentPreferenceKey
import app.aaps.pump.common.hw.rileylink.keys.RileyLinkStringPreferenceKey
import app.aaps.pump.common.hw.rileylink.keys.RileylinkBooleanPreferenceKey
import app.aaps.pump.medtronic.keys.MedtronicBooleanPreferenceKey
import app.aaps.pump.medtronic.keys.MedtronicIntPreferenceKey
import app.aaps.pump.medtronic.keys.MedtronicStringPreferenceKey

/**
 * Compose implementation of Medtronic preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class MedtronicPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.medtronic_name

    override val mainKeys: List<PreferenceKey> = listOf(
        MedtronicStringPreferenceKey.Serial,
        MedtronicStringPreferenceKey.PumpType,
        MedtronicStringPreferenceKey.PumpFrequency,
        MedtronicIntPreferenceKey.MaxBasal,
        MedtronicIntPreferenceKey.MaxBolus,
        MedtronicIntPreferenceKey.BolusDelay,
        RileyLinkStringPreferenceKey.Encoding,
        MedtronicStringPreferenceKey.BatteryType,
        RileyLinkIntentPreferenceKey.MacAddressSelector,
        RileylinkBooleanPreferenceKey.OrangeUseScanning,
        RileylinkBooleanPreferenceKey.ShowReportedBatteryLevel,
        MedtronicBooleanPreferenceKey.SetNeutralTemp
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
