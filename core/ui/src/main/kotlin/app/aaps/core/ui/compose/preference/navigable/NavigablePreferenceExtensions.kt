package app.aaps.core.ui.compose.preference.navigable

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.ui.compose.preference.ClickablePreferenceCategoryHeader
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState

/**
 * Helper function to add NavigablePreferenceContent inline in a LazyListScope.
 * This displays as one collapsible card with main content and subscreen items inside.
 *
 * Legacy pattern for NavigablePreferenceContent interface.
 *
 * @deprecated Legacy pattern - new code should use PreferenceSubScreenDef with addPreferenceSubScreenDef
 */
fun LazyListScope.addNavigablePreferenceContent(
    content: NavigablePreferenceContent,
    sectionState: PreferenceSectionState? = null
) {
    val sectionKey = "${content.keyPrefix}_main"
    item(key = sectionKey) {
        val isExpanded = sectionState?.isExpanded(sectionKey) ?: false
        CollapsibleCardSectionContent(
            titleResId = content.titleResId,
            summaryItems = content.effectiveSummaryItems(),
            expanded = isExpanded,
            onToggle = { sectionState?.toggle(sectionKey) }
        ) {
            // Show main content first (if any)
            content.mainContent?.invoke(sectionState)
            // Then show each subscreen as a simple collapsible section (no extra card)
            content.subscreens.forEach { subScreen ->
                val subSectionKey = "${content.keyPrefix}_${subScreen.key}"
                val isSubExpanded = sectionState?.isExpanded(subSectionKey) ?: false

                // Header without card
                ClickablePreferenceCategoryHeader(
                    titleResId = subScreen.titleResId,
                    summaryItems = subScreen.effectiveSummaryItems(),
                    expanded = isSubExpanded,
                    onToggle = { sectionState?.toggle(subSectionKey) },
                    insideCard = true
                )

                // Content without card wrapper
                if (isSubExpanded) {
                    subScreen.content(sectionState)
                }
            }
        }
    }
}
