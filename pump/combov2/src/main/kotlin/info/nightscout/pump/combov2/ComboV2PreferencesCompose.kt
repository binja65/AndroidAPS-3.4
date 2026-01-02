package info.nightscout.pump.combov2

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withActivity
import app.aaps.core.keys.interfaces.withClick
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import info.nightscout.pump.combov2.activities.ComboV2PairingActivity
import info.nightscout.pump.combov2.keys.ComboBooleanKey
import info.nightscout.pump.combov2.keys.ComboIntKey
import info.nightscout.pump.combov2.keys.ComboIntentKey

/**
 * Compose implementation of ComboV2 preferences.
 * Note: Pairing/unpairing activities require special handling.
 */
class ComboV2PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val onUnpairClick: () -> Unit = {}
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.combov2_title

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = listOf(
                ComboIntentKey.PairWithPump.withActivity(ComboV2PairingActivity::class.java),
                ComboIntentKey.UnpairPump.withClick(onUnpairClick),
                ComboIntKey.DiscoveryDuration,
                ComboBooleanKey.AutomaticReservoirEntry,
                ComboBooleanKey.AutomaticBatteryEntry,
                ComboBooleanKey.VerboseLogging
            ),
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
