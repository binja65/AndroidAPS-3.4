package app.aaps.pump.medtrum

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.pump.medtrum.keys.MedtrumBooleanKey
import app.aaps.pump.medtrum.keys.MedtrumIntKey
import app.aaps.pump.medtrum.keys.MedtrumStringKey

/**
 * Compose implementation of Medtrum preferences.
 */
class MedtrumPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Medtrum pump settings category
        val sectionKey = "${keyPrefix}_medtrum_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.medtrum_pump_setting,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
                AdaptiveStringPreferenceItem(
                    preferences = preferences,
                    config = config,
                    stringKey = MedtrumStringKey.MedtrumSnInput,
                    titleResId = R.string.sn_input_title
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = MedtrumBooleanKey.MedtrumWarningNotification,
                    titleResId = R.string.pump_warning_notification_title
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = MedtrumBooleanKey.MedtrumPatchExpiration,
                    titleResId = R.string.patch_expiration_title
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
        }
    }
}
