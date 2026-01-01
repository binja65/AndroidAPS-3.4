package app.aaps.compose.preferences

import androidx.compose.runtime.Composable
import app.aaps.R
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen

/**
 * Compose implementation of Local Alerts preferences.
 */
class AlertsPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.localalertsettings_title

    override val summaryItems: List<Int> = listOf(
        R.string.enable_missed_bg_readings_alert,
        R.string.enable_pump_unreachable_alert,
        R.string.enable_carbs_req_alert
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Enable missed BG readings alert
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.AlertMissedBgReading,
            titleResId = R.string.enable_missed_bg_readings_alert
        )

        // Stale data threshold (depends on AlertMissedBgReading)
        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.AlertsStaleDataThreshold,
            titleResId = app.aaps.plugins.sync.R.string.ns_alarm_stale_data_value_label
        )

        // Enable pump unreachable alert
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.AlertPumpUnreachable,
            titleResId = R.string.enable_pump_unreachable_alert
        )

        // Pump unreachable threshold (depends on AlertPumpUnreachable)
        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.AlertsPumpUnreachableThreshold,
            titleResId = R.string.pump_unreachable_threshold
        )

        // Enable carbs required alert
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.AlertCarbsRequired,
            titleResId = R.string.enable_carbs_req_alert
        )

        // Raise urgent alarms as Android notifications
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.AlertUrgentAsAndroidNotification,
            titleResId = app.aaps.core.ui.R.string.raise_notifications_as_android_notifications
        )

        // Gradually increase notification volume
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.AlertIncreaseVolume,
            titleResId = R.string.gradually_increase_notification_volume
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
