package app.aaps.core.ui.compose.preference

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.navigable.addNavigablePreferenceContent

/**
 * Helper function to add any preference content (legacy or new) inline in a LazyListScope.
 * Handles both NavigablePreferenceContent (legacy) and PreferenceSubScreenDef (new).
 */
fun LazyListScope.addPreferenceContent(
    content: Any,
    sectionState: PreferenceSectionState? = null,
    preferences: Preferences? = null,
    config: Config? = null,
    profileUtil: ProfileUtil? = null
) {
    when (content) {
        is NavigablePreferenceContent -> addNavigablePreferenceContent(content, sectionState)
        is PreferenceSubScreenDef -> addPreferenceSubScreenDef(content, sectionState, preferences, config, profileUtil)
    }
}

/**
 * Helper function to add PreferenceSubScreenDef inline in a LazyListScope.
 * This displays as one collapsible card with main content and nested subscreens inside.
 * Content is rendered using the new pattern (no NavigablePreferenceContent interface).
 */
fun LazyListScope.addPreferenceSubScreenDef(
    def: PreferenceSubScreenDef,
    sectionState: PreferenceSectionState? = null,
    preferences: Preferences? = null,
    config: Config? = null,
    profileUtil: ProfileUtil? = null
) {
    val sectionKey = "${def.key}_main"
    item(key = sectionKey) {
        val isExpanded = sectionState?.isExpanded(sectionKey) ?: false
        CollapsibleCardSectionContent(
            titleResId = def.titleResId,
            summaryItems = def.effectiveSummaryItems(),
            expanded = isExpanded,
            onToggle = { sectionState?.toggle(sectionKey) }
        ) {
            // Show custom content first (if any)
            if (def.customContent != null) {
                def.customContent.invoke(sectionState)
            } else {
                // Render items in order, preserving the original structure
                def.effectiveItems.forEach { item ->
                    when (item) {
                        is PreferenceKey -> {
                            // Render individual preference key
                            if (preferences != null && config != null) {
                                @Suppress("DEPRECATION")
                                AdaptivePreferenceListForListKeys(
                                    keys = listOf(item),
                                    preferences = preferences,
                                    config = config,
                                    profileUtil = profileUtil
                                )
                            }
                        }
                        is PreferenceSubScreenDef -> {
                            // Render nested subscreen as simple collapsible section (no extra card)
                            val subSectionKey = "${def.key}_${item.key}"
                            val isSubExpanded = sectionState?.isExpanded(subSectionKey) ?: false

                            // Header without card
                            ClickablePreferenceCategoryHeader(
                                titleResId = item.titleResId,
                                summaryItems = item.effectiveSummaryItems(),
                                expanded = isSubExpanded,
                                onToggle = { sectionState?.toggle(subSectionKey) },
                                insideCard = true
                            )

                            // Content without card wrapper
                            if (isSubExpanded) {
                                if (item.customContent != null) {
                                    item.customContent.invoke(sectionState)
                                } else if (preferences != null && config != null) {
                                    // Auto-render nested subscreen items (including DialogIntentPreference)
                                    if (item.effectiveItems.isNotEmpty()) {
                                        Column(
                                            modifier = Modifier.padding(start = 16.dp)
                                        ) {
                                            AdaptivePreferenceList(
                                                items = item.effectiveItems,
                                                preferences = preferences,
                                                config = config,
                                                profileUtil = profileUtil,
                                                onNavigateToSubScreen = null // Nested subscreens not supported here
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
