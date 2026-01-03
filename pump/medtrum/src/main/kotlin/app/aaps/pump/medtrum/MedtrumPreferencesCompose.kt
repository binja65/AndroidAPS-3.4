package app.aaps.pump.medtrum

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.medtrum.keys.MedtrumBooleanKey
import app.aaps.pump.medtrum.keys.MedtrumIntKey
import app.aaps.pump.medtrum.keys.MedtrumStringKey

/**
 * Compose implementation of Medtrum preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 * Serial number input is disabled when pump is initialized.
 */
class MedtrumPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val isPumpInitialized: () -> Boolean
) : NavigablePreferenceContent {

    private val visibilityContext = object : PreferenceVisibilityContext {
        override val preferences: Preferences = this@MedtrumPreferencesCompose.preferences
        override val isPatchPump: Boolean = true // Medtrum is a patch pump
        override val isBatteryReplaceable: Boolean = false
        override val isBatteryChangeLoggingEnabled: Boolean = false
        override val advancedFilteringSupported: Boolean = false
        override val isPumpInitialized: Boolean get() = this@MedtrumPreferencesCompose.isPumpInitialized()
    }

    override val titleResId: Int = R.string.medtrum

    override val mainKeys: List<PreferenceKey> = listOf(
        MedtrumStringKey.MedtrumSnInput,
        MedtrumStringKey.MedtrumAlarmSettings,
        MedtrumBooleanKey.MedtrumWarningNotification,
        MedtrumBooleanKey.MedtrumPatchExpiration,
        MedtrumIntKey.MedtrumPumpExpiryWarningHours,
        MedtrumIntKey.MedtrumHourlyMaxInsulin,
        MedtrumIntKey.MedtrumDailyMaxInsulin
    )

    private val advancedKeys: List<PreferenceKey> = listOf(
        MedtrumBooleanKey.MedtrumScanOnConnectionErrors
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config,
            visibilityContext = visibilityContext
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        PreferenceSubScreen(
            key = "medtrum_advanced",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            keys = advancedKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = advancedKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
