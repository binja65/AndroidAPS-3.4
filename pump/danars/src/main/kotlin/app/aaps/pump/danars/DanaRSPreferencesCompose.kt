package app.aaps.pump.danars

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withActivity
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
 * Note: BT selector requires activity launch.
 */
class DanaRSPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.danarspump

    override val mainKeys: List<PreferenceKey> = listOf(
        DanaIntentKey.BtSelector.withActivity(BLEScanActivity::class.java),
        DanaStringKey.Password,
        DanaIntKey.BolusSpeed,
        DanaBooleanKey.LogInsulinChange,
        DanaBooleanKey.LogCannulaChange
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
