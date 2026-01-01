package app.aaps.plugins.main.general.smsCommunicator

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.main.R
import app.aaps.plugins.main.general.smsCommunicator.keys.SmsIntentKey

/**
 * Compose implementation of SMS Communicator preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class SmsCommunicatorPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.smscommunicator

    override val mainKeys: List<PreferenceKey> = listOf(
        StringKey.SmsAllowedNumbers,
        BooleanKey.SmsAllowRemoteCommands,
        IntKey.SmsRemoteBolusDistance,
        StringKey.SmsOtpPassword,
        SmsIntentKey.OtpSetup,
        BooleanKey.SmsReportPumpUnreachable
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
