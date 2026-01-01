package app.aaps.plugins.main.general.smsCommunicator

import androidx.compose.runtime.Composable
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
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.main.R
import app.aaps.plugins.main.general.smsCommunicator.activities.SmsCommunicatorOtpActivity

/**
 * Compose implementation of SMS Communicator preferences.
 */
class SmsCommunicatorPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.smscommunicator

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
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

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
