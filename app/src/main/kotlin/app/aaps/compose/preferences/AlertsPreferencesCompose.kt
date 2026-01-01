package app.aaps.compose.preferences

import androidx.compose.runtime.Composable
import app.aaps.R
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen

/**
 * Compose implementation of Local Alerts preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class AlertsPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.localalertsettings_title

    override val mainKeys: List<PreferenceKey> = listOf(
        BooleanKey.AlertMissedBgReading,
        IntKey.AlertsStaleDataThreshold,
        BooleanKey.AlertPumpUnreachable,
        IntKey.AlertsPumpUnreachableThreshold,
        BooleanKey.AlertCarbsRequired,
        BooleanKey.AlertUrgentAsAndroidNotification,
        BooleanKey.AlertIncreaseVolume
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
