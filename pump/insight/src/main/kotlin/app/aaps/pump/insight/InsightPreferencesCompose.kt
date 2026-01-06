package app.aaps.pump.insight

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withActivity
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.insight.app_layer.activities.InsightPairingInformationActivity
import app.aaps.pump.insight.keys.InsightBooleanKey
import app.aaps.pump.insight.keys.InsightIntKey
import app.aaps.pump.insight.keys.InsightIntentKey

/**
 * Compose implementation of Insight preferences.
 * Note: Pairing activity requires special handling.
 */
class InsightPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.insight_local

    override val mainKeys: List<PreferenceKey> = listOf(
        InsightIntentKey.InsightPairing.withActivity(InsightPairingInformationActivity::class.java),
        InsightBooleanKey.LogReservoirChanges,
        InsightBooleanKey.LogTubeChanges,
        InsightBooleanKey.LogSiteChanges,
        InsightBooleanKey.LogBatteryChanges,
        InsightBooleanKey.LogOperatingModeChanges,
        InsightBooleanKey.LogAlerts,
        InsightBooleanKey.EnableTbrEmulation,
        InsightBooleanKey.DisableVibration,
        InsightBooleanKey.DisableVibrationAuto,
        InsightIntKey.MinRecoveryDuration,
        InsightIntKey.MaxRecoveryDuration,
        InsightIntKey.DisconnectDelay
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
