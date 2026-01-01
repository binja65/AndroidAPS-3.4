package app.aaps.pump.medtrum

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.medtrum.keys.MedtrumBooleanKey
import app.aaps.pump.medtrum.keys.MedtrumIntKey
import app.aaps.pump.medtrum.keys.MedtrumStringKey

/**
 * Compose implementation of Medtrum preferences.
 */
class MedtrumPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    companion object {

        // Alarm settings - full list (pump-specific filtering would be handled dynamically)
        private val alarmEntries = mapOf(
            "0" to "Light, vibrate and beep",
            "1" to "Light and vibrate",
            "2" to "Light and beep",
            "3" to "Light",
            "4" to "Vibrate and beep",
            "5" to "Vibrate",
            "6" to "Beep",
            "7" to "Silent"
        )
    }

    override val titleResId: Int = R.string.medtrum

    override val summaryItems: List<Int> = listOf(
        R.string.sn_input_title,
        R.string.alarm_setting_title,
        R.string.pump_warning_notification_title
    )

    // Main content shown at top level
    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptiveStringPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = MedtrumStringKey.MedtrumSnInput,
            titleResId = R.string.sn_input_title,
            summaryResId = R.string.sn_input_summary
        )

        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = MedtrumStringKey.MedtrumAlarmSettings,
            titleResId = R.string.alarm_setting_title,
            entries = alarmEntries
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = MedtrumBooleanKey.MedtrumWarningNotification,
            titleResId = R.string.pump_warning_notification_title,
            summaryResId = R.string.pump_warning_notification_summary
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = MedtrumBooleanKey.MedtrumPatchExpiration,
            titleResId = R.string.patch_expiration_title,
            summaryResId = R.string.patch_expiration_summary
        )

        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = MedtrumIntKey.MedtrumPumpExpiryWarningHours,
            titleResId = R.string.pump_warning_expiry_hour_title
        )

        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = MedtrumIntKey.MedtrumHourlyMaxInsulin,
            titleResId = R.string.hourly_max_insulin_title
        )

        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = MedtrumIntKey.MedtrumDailyMaxInsulin,
            titleResId = R.string.daily_max_insulin_title
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Advanced settings subscreen
        PreferenceSubScreen(
            key = "medtrum_advanced",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            summaryItems = listOf(
                R.string.scan_on_connection_error_title
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = MedtrumBooleanKey.MedtrumScanOnConnectionErrors,
                titleResId = R.string.scan_on_connection_error_title,
                summaryResId = R.string.scan_on_connection_error_summary
            )
        }
    )
}
