package info.nightscout.pump.combov2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.PreferenceKey
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
import kotlinx.coroutines.flow.StateFlow

/**
 * Compose implementation of ComboV2 preferences.
 * Pairing state is observed reactively to enable/disable pair/unpair buttons.
 */
class ComboV2PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val pairedStateFlow: StateFlow<Boolean>,
    private val onUnpairClick: () -> Unit = {}
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.combov2_title

    override val mainKeys: List<PreferenceKey> = listOf(
        ComboIntentKey.PairWithPump.withActivity(ComboV2PairingActivity::class.java),
        ComboIntentKey.UnpairPump.withClick(onUnpairClick),
        ComboIntKey.DiscoveryDuration,
        ComboBooleanKey.AutomaticReservoirEntry,
        ComboBooleanKey.AutomaticBatteryEntry,
        ComboBooleanKey.VerboseLogging
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Collect pairing state reactively
        val isPaired by pairedStateFlow.collectAsState()

        // Create visibility context with current pairing state
        val visibilityContext = object : PreferenceVisibilityContext {
            override val preferences: Preferences = this@ComboV2PreferencesCompose.preferences
            override val isPatchPump: Boolean = false
            override val isBatteryReplaceable: Boolean = true
            override val isBatteryChangeLoggingEnabled: Boolean = false
            override val advancedFilteringSupported: Boolean = false
            override val isPumpPaired: Boolean = isPaired
        }

        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config,
            visibilityContext = visibilityContext
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
