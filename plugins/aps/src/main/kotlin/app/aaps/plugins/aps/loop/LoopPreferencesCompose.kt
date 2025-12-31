package app.aaps.plugins.aps.loop

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.sharedPreferences.SP
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent

/**
 * Compose implementation of Loop preferences.
 */
class LoopPreferencesCompose(
    private val sp: SP
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
            // Open Mode Min Change - needs AdaptiveIntPreference compose equivalent
            // IntKey.LoopOpenModeMinChange with dialog message
            }
        }
    }
}
