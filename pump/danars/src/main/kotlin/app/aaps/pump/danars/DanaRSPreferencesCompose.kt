package app.aaps.pump.danars

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveActivityPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.dana.R
import app.aaps.pump.dana.keys.DanaBooleanKey
import app.aaps.pump.dana.keys.DanaIntKey
import app.aaps.pump.dana.keys.DanaIntentKey
import app.aaps.pump.dana.keys.DanaStringKey
import app.aaps.pump.danars.activities.BLEScanActivity

/**
 * Compose implementation of DanaRS preferences.
 * Note: BT selector and bolus speed require special handling.
 */
class DanaRSPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    companion object {

        private val bolusSpeedEntries = listOf("12 s/U", "30 s/U", "60 s/U")
        private val bolusSpeedValues = listOf(0, 1, 2)
    }

    override val titleResId: Int = R.string.danarspump

    override val mainKeys: List<PreferenceKey> = listOf(
        DanaStringKey.Password,
        DanaIntKey.BolusSpeed,
        DanaBooleanKey.LogInsulinChange,
        DanaBooleanKey.LogCannulaChange
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // BT selector - requires activity launch
        AdaptiveActivityPreferenceItem(
            preferences = preferences,
            intentKey = DanaIntentKey.BtSelector,
            titleResId = R.string.selectedpump,
            activityClass = BLEScanActivity::class.java
        )

        // Password - can use key-based
        AdaptivePreferenceList(
            keys = listOf(DanaStringKey.Password),
            preferences = preferences,
            config = config
        )

        // Bolus speed - requires custom entries
        AdaptiveListIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = DanaIntKey.BolusSpeed,
            titleResId = R.string.bolusspeed,
            entries = bolusSpeedEntries,
            entryValues = bolusSpeedValues
        )

        // Log preferences - can use key-based
        AdaptivePreferenceList(
            keys = listOf(
                DanaBooleanKey.LogInsulinChange,
                DanaBooleanKey.LogCannulaChange
            ),
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
