package app.aaps.plugins.source

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState

/**
 * Compose implementation of Random BG source preferences.
 */
class RandomBgPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Random BG settings category
        val sectionKey = "${keyPrefix}_bg_source_upload_settings"
        item {
            val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.random_bg,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(sectionKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.BgSourceUploadToNs,
                    titleResId = app.aaps.core.ui.R.string.do_ns_upload_title
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = IntKey.BgSourceRandomInterval,
                    titleResId = R.string.bg_generation_interval_minutes
                )
            }
        }
    }
}
