package app.aaps.pump.omnipod.eros

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.pump.common.hw.rileylink.keys.RileylinkBooleanPreferenceKey
import app.aaps.pump.omnipod.common.keys.OmnipodBooleanPreferenceKey
import app.aaps.pump.omnipod.common.keys.OmnipodIntPreferenceKey
import app.aaps.pump.omnipod.eros.keys.ErosBooleanPreferenceKey

/**
 * Compose implementation of Omnipod Eros preferences.
 */
class OmnipodErosPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val rileyLinkConfigActivityClass: Class<out Activity>? = null
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // RileyLink Category
        val rileyLinkKey = "${keyPrefix}_omnipod_eros_riley_link"
        item {
            val isExpanded = sectionState?.isExpanded(rileyLinkKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.omnipod_eros_preferences_category_riley_link,
                summaryItems = listOf(
                    app.aaps.pump.common.hw.rileylink.R.string.rileylink_configuration,
                    app.aaps.pump.common.hw.rileylink.R.string.orange_use_scanning_level
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(rileyLinkKey) }
            ) {
                // RileyLink Configuration Activity
                rileyLinkConfigActivityClass?.let { activityClass ->
                    RileyLinkConfigPreferenceItem(
                        titleResId = app.aaps.pump.common.hw.rileylink.R.string.rileylink_configuration,
                        activityClass = activityClass
                    )
                }

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = RileylinkBooleanPreferenceKey.OrangeUseScanning,
                    titleResId = app.aaps.pump.common.hw.rileylink.R.string.orange_use_scanning_level,
                    summaryResId = app.aaps.pump.common.hw.rileylink.R.string.orange_use_scanning_level_summary
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = RileylinkBooleanPreferenceKey.ShowReportedBatteryLevel,
                    titleResId = app.aaps.pump.common.hw.rileylink.R.string.riley_link_show_battery_level,
                    summaryResId = app.aaps.pump.common.hw.rileylink.R.string.riley_link_show_battery_level_summary
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = ErosBooleanPreferenceKey.BatteryChangeLogging,
                    titleResId = R.string.omnipod_eros_preferences_battery_change_logging_enabled,
                    summaryResId = app.aaps.pump.common.hw.rileylink.R.string.riley_link_show_battery_level_summary
                )
            }
        }

        // Beep Category
        val beepKey = "${keyPrefix}_omnipod_eros_beeps"
        item {
            val isExpanded = sectionState?.isExpanded(beepKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_confirmation_beeps,
                summaryItems = listOf(
                    app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_bolus_beeps_enabled,
                    app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_basal_beeps_enabled,
                    app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_smb_beeps_enabled
                ),
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
            }
        }

        // Alerts Category
        val alertsKey = "${keyPrefix}_omnipod_eros_alerts"
        item {
            val isExpanded = sectionState?.isExpanded(alertsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_alerts,
                summaryItems = listOf(
                    app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_expiration_reminder_enabled,
                    app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_low_reservoir_alert_enabled
                ),
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

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = OmnipodBooleanPreferenceKey.AutomaticallyAcknowledgeAlerts,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_automatically_silence_alerts
                )
            }
        }

        // Notifications Category
        val notificationsKey = "${keyPrefix}_omnipod_eros_notifications"
        item {
            val isExpanded = sectionState?.isExpanded(notificationsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_notifications,
                summaryItems = listOf(
                    app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_notification_uncertain_tbr_sound_enabled,
                    app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_notification_uncertain_smb_sound_enabled
                ),
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
            }
        }

        // Other Settings Category
        val otherKey = "${keyPrefix}_omnipod_eros_other_settings"
        item {
            val isExpanded = sectionState?.isExpanded(otherKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_other,
                summaryItems = listOf(
                    app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_suspend_delivery_button_enabled,
                    R.string.omnipod_eros_preferences_pulse_log_button_enabled
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(otherKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = ErosBooleanPreferenceKey.ShowSuspendDeliveryButton,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_suspend_delivery_button_enabled
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = ErosBooleanPreferenceKey.ShowPulseLogButton,
                    titleResId = R.string.omnipod_eros_preferences_pulse_log_button_enabled
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = ErosBooleanPreferenceKey.ShowRileyLinkStatsButton,
                    titleResId = R.string.omnipod_eros_preferences_riley_link_stats_button_enabled
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = ErosBooleanPreferenceKey.TimeChangeEnabled,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_time_change_enabled
                )
            }
        }
    }
}

/**
 * Composable preference for launching RileyLink configuration activity.
 */
@Composable
private fun RileyLinkConfigPreferenceItem(
    titleResId: Int,
    activityClass: Class<out Activity>
) {
    val context = LocalContext.current
    Preference(
        title = { Text(stringResource(titleResId)) },
        onClick = {
            context.startActivity(Intent(context, activityClass))
        }
    )
}
