package app.aaps.pump.danars

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.pump.dana.R
import app.aaps.pump.dana.keys.DanaBooleanKey

/**
 * Compose implementation of DanaRS preferences.
 * Note: Bluetooth device selection is handled separately as it requires runtime permissions and intent.
 */
class DanaRSPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // DanaRS pump settings category
        val sectionKey = "${keyPrefix}_danars_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.danarspump,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = DanaBooleanKey.LogInsulinChange,
                    titleResId = R.string.rs_loginsulinchange_title,
                    summaryResId = R.string.rs_loginsulinchange_summary
                )

                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = DanaBooleanKey.LogCannulaChange,
                    titleResId = R.string.rs_logcanulachange_title,
                    summaryResId = R.string.rs_logcanulachange_summary
                )
            }
        }
    }
}
