package app.aaps.pump.danars

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveActivityPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
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

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptiveActivityPreferenceItem(
            preferences = preferences,
            intentKey = DanaIntentKey.BtSelector,
            titleResId = R.string.selectedpump,
            activityClass = BLEScanActivity::class.java
        )

        AdaptiveStringPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = DanaStringKey.Password,
            titleResId = R.string.danars_password_title
        )

        AdaptiveListIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = DanaIntKey.BolusSpeed,
            titleResId = R.string.bolusspeed,
            entries = bolusSpeedEntries,
            entryValues = bolusSpeedValues
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = DanaBooleanKey.LogInsulinChange,
            titleResId = R.string.rs_loginsulinchange_title,
            summaryResId = R.string.rs_loginsulinchange_summary
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = DanaBooleanKey.LogCannulaChange,
            titleResId = R.string.rs_logcanulachange_title,
            summaryResId = R.string.rs_logcanulachange_summary
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
