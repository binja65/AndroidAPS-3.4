package app.aaps.pump.eopatch

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.eopatch.keys.EopatchBooleanKey
import app.aaps.pump.eopatch.keys.EopatchIntKey

/**
 * Compose implementation of Eopatch preferences.
 * Note: Low reservoir and expiration reminders require custom entries.
 */
class EopatchPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    companion object {

        private val lowReservoirEntries = listOf("10 U", "15 U", "20 U", "25 U", "30 U", "35 U", "40 U", "45 U", "50 U")
        private val lowReservoirValues = listOf(10, 15, 20, 25, 30, 35, 40, 45, 50)
        private val expirationReminderEntries = (1..24).map { "$it hr" }
        private val expirationReminderValues = (1..24).toList()
    }

    override val titleResId: Int = R.string.eopatch

    override val mainKeys: List<PreferenceKey> = listOf(
        EopatchIntKey.LowReservoirReminder,
        EopatchIntKey.ExpirationReminder,
        EopatchBooleanKey.BuzzerReminder
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Low reservoir - requires custom entries
        AdaptiveListIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = EopatchIntKey.LowReservoirReminder,
            titleResId = R.string.low_reservoir,
            entries = lowReservoirEntries,
            entryValues = lowReservoirValues
        )

        // Expiration reminder - requires custom entries
        AdaptiveListIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = EopatchIntKey.ExpirationReminder,
            titleResId = R.string.patch_expiration_reminders,
            entries = expirationReminderEntries,
            entryValues = expirationReminderValues
        )

        // Buzzer reminder - can use key-based
        AdaptivePreferenceList(
            keys = listOf(EopatchBooleanKey.BuzzerReminder),
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
