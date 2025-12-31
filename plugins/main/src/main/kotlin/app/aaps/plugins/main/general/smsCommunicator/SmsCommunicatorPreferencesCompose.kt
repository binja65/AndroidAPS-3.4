package app.aaps.plugins.main.general.smsCommunicator

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.IntentKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveActivityPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.plugins.main.R
import app.aaps.plugins.main.general.smsCommunicator.activities.SmsCommunicatorOtpActivity

/**
 * Compose implementation of SMS Communicator preferences.
 */
class SmsCommunicatorPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // SMS Communicator category
        val smsSettingsKey = "${keyPrefix}_smscommunicator_settings"
        item {
            val isExpanded = sectionState?.isExpanded(smsSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.smscommunicator,
                summaryItems = listOf(
                    R.string.smscommunicator_allowednumbers,
                    R.string.smscommunicator_remote_commands_allowed,
                    R.string.smscommunicator_otp_pin,
                    R.string.smscommunicator_pump_unreachable
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(smsSettingsKey) }
            ) {
                AdaptiveStringPreferenceItem(
                    preferences = preferences,
                    config = config,
                    stringKey = StringKey.SmsAllowedNumbers,
                    titleResId = R.string.smscommunicator_allowednumbers,
                    summaryResId = R.string.smscommunicator_allowednumbers_summary
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.SmsAllowRemoteCommands,
                    titleResId = R.string.smscommunicator_remote_commands_allowed
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = IntKey.SmsRemoteBolusDistance,
                    titleResId = R.string.smscommunicator_remote_bolus_min_distance
                )

                AdaptiveStringPreferenceItem(
                    preferences = preferences,
                    config = config,
                    stringKey = StringKey.SmsOtpPassword,
                    titleResId = R.string.smscommunicator_otp_pin,
                    summaryResId = R.string.smscommunicator_otp_pin_summary,
                    isPassword = true
                )

                AdaptiveActivityPreferenceItem(
                    preferences = preferences,
                    intentKey = IntentKey.SmsOtpSetup,
                    titleResId = R.string.smscommunicator_tab_otp_label,
                    activityClass = SmsCommunicatorOtpActivity::class.java
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.SmsReportPumpUnreachable,
                    titleResId = R.string.smscommunicator_pump_unreachable,
                    summaryResId = R.string.smscommunicator_report_pump_unreachable_summary
                )
            }
        }
    }
}
