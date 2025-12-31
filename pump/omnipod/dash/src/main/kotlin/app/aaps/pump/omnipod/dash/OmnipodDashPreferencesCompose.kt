package app.aaps.pump.omnipod.dash

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.pump.omnipod.common.keys.OmnipodBooleanPreferenceKey
import app.aaps.pump.omnipod.common.keys.OmnipodIntPreferenceKey
import app.aaps.pump.omnipod.dash.keys.DashBooleanPreferenceKey

/**
 * Compose implementation of Omnipod DASH preferences.
 */
class OmnipodDashPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Beep Category
        val beepKey = "${keyPrefix}_omnipod_dash_beeps"
        item {
            val isExpanded = sectionState?.isExpanded(beepKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_confirmation_beeps,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(beepKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.BolusBeepsEnabled,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_bolus_beeps_enabled
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.BasalBeepsEnabled,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_basal_beeps_enabled
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.SmbBeepsEnabled,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_smb_beeps_enabled
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.TbrBeepsEnabled,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_tbr_beeps_enabled
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = DashBooleanPreferenceKey.UseBonding,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_dash_use_bonding
                )
            }
        }

        // Alerts Category
        val alertsKey = "${keyPrefix}_omnipod_dash_alerts"
        item {
            val isExpanded = sectionState?.isExpanded(alertsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_alerts,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(alertsKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.ExpirationReminder,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_expiration_reminder_enabled,
                    summaryResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_expiration_reminder_enabled_summary
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = OmnipodIntPreferenceKey.ExpirationReminderHours,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_expiration_reminder_hours_before_expiry
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.ExpirationAlarm,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_expiration_alarm_enabled,
                    summaryResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_expiration_alarm_enabled_summary
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = OmnipodIntPreferenceKey.ExpirationAlarmHours,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_expiration_alarm_hours_before_shutdown
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.LowReservoirAlert,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_low_reservoir_alert_enabled
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = OmnipodIntPreferenceKey.LowReservoirAlertUnits,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_low_reservoir_alert_units
                )

            }
        }

        // Notifications Category
        val notificationsKey = "${keyPrefix}_omnipod_dash_notifications"
        item {
            val isExpanded = sectionState?.isExpanded(notificationsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_notifications,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(notificationsKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.SoundUncertainTbrNotification,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_notification_uncertain_tbr_sound_enabled
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.SoundUncertainSmbNotification,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_notification_uncertain_smb_sound_enabled
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.SoundUncertainBolusNotification,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_notification_uncertain_bolus_sound_enabled
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = DashBooleanPreferenceKey.SoundDeliverySuspendedNotification,
                    titleResId = R.string.omnipod_common_preferences_notification_delivery_suspended_sound_enabled
                )
            }
        }
    }
}
