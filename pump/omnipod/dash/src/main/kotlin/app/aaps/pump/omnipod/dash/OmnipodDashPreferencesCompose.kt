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

/**
 * Compose implementation of Omnipod DASH preferences.
 */
class OmnipodDashPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Omnipod DASH pump settings category
        val sectionKey = "${keyPrefix}_omnipod_dash_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.omnipod_dash_name,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
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
                    booleanKey = OmnipodBooleanPreferenceKey.AutomaticallyAcknowledgeAlerts,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_automatically_silence_alerts
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = OmnipodIntPreferenceKey.ExpirationReminderHours,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_expiration_reminder_hours_before_expiry
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = OmnipodIntPreferenceKey.LowReservoirAlertUnits,
                    titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_low_reservoir_alert_units
                )
            }
        }
    }
}
