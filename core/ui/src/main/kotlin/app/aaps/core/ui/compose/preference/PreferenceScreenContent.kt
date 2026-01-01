package app.aaps.core.ui.compose.preference

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.core.os.bundleOf

/**
 * State holder for collapsible preference sections.
 * Tracks which sections are expanded and persists across configuration changes.
 */
class PreferenceSectionState(
    private val expandedSections: SnapshotStateMap<String, Boolean> = mutableStateMapOf()
) {

    /**
     * Check if a section is expanded (default: false - collapsed)
     */
    fun isExpanded(sectionKey: String): Boolean = expandedSections[sectionKey] ?: false

    /**
     * Toggle the expanded state of a section
     */
    fun toggle(sectionKey: String) {
        expandedSections[sectionKey] = !isExpanded(sectionKey)
    }

    /**
     * Set the expanded state of a section
     */
    fun setExpanded(sectionKey: String, expanded: Boolean) {
        expandedSections[sectionKey] = expanded
    }

    companion object {

        val Saver: Saver<PreferenceSectionState, Bundle> = Saver(
            save = { state ->
                bundleOf(*state.expandedSections.map { (k, v) -> k to v }.toTypedArray())
            },
            restore = { bundle ->
                PreferenceSectionState(
                    mutableStateMapOf<String, Boolean>().apply {
                        bundle.keySet().forEach { key ->
                            put(key, bundle.getBoolean(key))
                        }
                    }
                )
            }
        )
    }
}

/**
 * Remember and save preference section state across configuration changes
 */
@Composable
fun rememberPreferenceSectionState(): PreferenceSectionState {
    return rememberSaveable(saver = PreferenceSectionState.Saver) {
        PreferenceSectionState()
    }
}
