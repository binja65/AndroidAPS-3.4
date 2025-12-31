package app.aaps.plugins.main.general.smsCommunicator

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.sharedPreferences.SP
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntentKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.adaptiveActivityPreference
import app.aaps.core.ui.compose.preference.collapsibleSection
import app.aaps.core.ui.compose.preference.stringTextFieldPreference
import app.aaps.core.ui.compose.preference.switchPreference
import app.aaps.plugins.main.R
import app.aaps.plugins.main.general.smsCommunicator.activities.SmsCommunicatorOtpActivity

/**
 * Compose implementation of SMS Communicator preferences.
 */
class SmsCommunicatorPreferencesCompose(
    private val sp: SP,
    private val preferences: Preferences
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // SMS Communicator category
        collapsibleSection(
            sectionState = sectionState,
            sectionKey = "${keyPrefix}_smscommunicator_settings",
            titleResId = R.string.smscommunicator
        ) {
            // Allowed phone numbers
            stringTextFieldPreference(
                sp = sp,
                key = StringKey.SmsAllowedNumbers.key,
                defaultValue = StringKey.SmsAllowedNumbers.defaultValue,
                titleResId = R.string.smscommunicator_allowednumbers,
                summaryResId = R.string.smscommunicator_allowednumbers_summary
            )

            // Remote commands allowed
            switchPreference(
                sp = sp,
                key = BooleanKey.SmsAllowRemoteCommands.key,
                defaultValue = BooleanKey.SmsAllowRemoteCommands.defaultValue,
                titleResId = R.string.smscommunicator_remote_commands_allowed
            )

            // Remote bolus min distance - IntKey.SmsRemoteBolusDistance
            // Needs intTextFieldPreference

            // OTP Password
            stringTextFieldPreference(
                sp = sp,
                key = StringKey.SmsOtpPassword.key,
                defaultValue = StringKey.SmsOtpPassword.defaultValue,
                titleResId = R.string.smscommunicator_otp_pin,
                summaryResId = R.string.smscommunicator_otp_pin_summary,
                isPassword = true
            )

            // OTP Setup
            adaptiveActivityPreference(
                preferences = preferences,
                intentKey = IntentKey.SmsOtpSetup,
                titleResId = R.string.smscommunicator_tab_otp_label,
                activityClass = SmsCommunicatorOtpActivity::class.java,
                keyPrefix = keyPrefix
            )

            // Report pump unreachable
            switchPreference(
                sp = sp,
                key = BooleanKey.SmsReportPumpUnreachable.key,
                defaultValue = BooleanKey.SmsReportPumpUnreachable.defaultValue,
                titleResId = R.string.smscommunicator_pump_unreachable,
                summaryResId = R.string.smscommunicator_report_pump_unreachable_summary
            )
        }
    }
}
