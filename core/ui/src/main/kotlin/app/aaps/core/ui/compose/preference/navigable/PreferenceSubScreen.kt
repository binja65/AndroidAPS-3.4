package app.aaps.core.ui.compose.preference.navigable

import androidx.compose.runtime.Composable
import app.aaps.core.keys.interfaces.PreferenceItem
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.ui.compose.preference.PreferenceSectionState

/**
 * Legacy: Represents a preference subscreen that can be navigated to.
 * Kept for backward compatibility. New code should use PreferenceSubScreenDef.
 *
 * @param key Unique key for this subscreen
 * @param titleResId String resource ID for the screen title
 * @param keys List of preference keys - summaryItems is auto-derived from keys' titleResId (preferred)
 * @param summaryItems Direct list of string resource IDs for summary (legacy, use keys instead)
 * @param summaryResId Optional string resource ID for summary shown in parent list
 * @param content Composable lambda that provides the preference content for this subscreen
 * @deprecated Legacy pattern - new code should use PreferenceSubScreenDef instead
 */
class PreferenceSubScreen(
    val key: String,
    val titleResId: Int,
    val keys: List<PreferenceKey> = emptyList(),
    val summaryItems: List<Int> = emptyList(),
    val summaryResId: Int? = null,
    val content: @Composable (PreferenceSectionState?) -> Unit
) : PreferenceItem {
    /** Effective summary items - from keys if provided, otherwise direct summaryItems */
    fun effectiveSummaryItems(): List<Int> =
        if (keys.isNotEmpty()) keys.map { it.titleResId }.filter { it != 0 }
        else summaryItems
}
