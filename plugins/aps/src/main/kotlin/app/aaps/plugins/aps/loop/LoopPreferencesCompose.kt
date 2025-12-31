package app.aaps.plugins.aps.loop

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.plugins.aps.R

/**
 * Compose implementation of Loop preferences.
 */
class LoopPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Loop category
        val loopSettingsKey = "${keyPrefix}_loop_settings"
        item {
            val isExpanded = sectionState?.isExpanded(loopSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.loop,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(loopSettingsKey) }
            ) {
                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = IntKey.LoopOpenModeMinChange,
                    titleResId = R.string.loop_open_mode_min_change
                )
            }
        }
    }
}
