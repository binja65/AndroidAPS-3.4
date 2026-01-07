/*
 * Adaptive Preference List for Jetpack Compose
 * Provides generic rendering for lists of PreferenceItems
 */

package app.aaps.core.ui.compose.preference

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.interfaces.PreferenceItem
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceItem

/**
 * Renders a list of preference items (keys, subscreens, custom items).
 * This is the enhanced version that handles all PreferenceItem types.
 *
 * Supported types:
 * - PreferenceKey: Rendered with AdaptivePreferenceItem
 * - PreferenceSubScreenDef: Rendered as navigation item
 * - DialogIntentPreference: Rendered with dialog handling
 * - ComposablePreferenceItem: Renders custom composable
 *
 * @param items List of PreferenceItems to render
 * @param preferences The Preferences instance
 * @param config The Config instance
 * @param profileUtil Required for UnitDoublePreferenceKey
 * @param visibilityContext Optional context for evaluating runtime visibility conditions
 * @param onNavigateToSubScreen Callback for navigating to subscreens
 */
@Composable
fun AdaptivePreferenceList(
    items: List<PreferenceItem>,
    preferences: Preferences,
    config: Config,
    profileUtil: ProfileUtil? = null,
    visibilityContext: PreferenceVisibilityContext? = null,
    onNavigateToSubScreen: ((PreferenceSubScreenDef) -> Unit)? = null
) {
    items.forEach { item ->
        when (item) {
            is PreferenceKey -> {
                // Handle standard preference keys
                if (visibilityContext == null || item.visibility.isVisible(visibilityContext)) {
                    AdaptivePreferenceItem(
                        key = item,
                        preferences = preferences,
                        config = config,
                        profileUtil = profileUtil,
                        visibilityContext = visibilityContext
                    )
                }
            }

            is PreferenceSubScreenDef -> {
                // Handle subscreens as navigation items
                if (onNavigateToSubScreen != null) {
                    NavigablePreferenceItem(
                        titleResId = item.titleResId,
                        summaryResId = item.summaryResId,
                        summaryItems = item.effectiveSummaryItems(),
                        onClick = { onNavigateToSubScreen(item) }
                    )
                }
            }

            is DialogIntentPreference -> {
                // Handle dialog preferences
                item.Render { keyToRender ->
                    AdaptivePreferenceItem(
                        key = keyToRender,
                        preferences = preferences,
                        config = config,
                        profileUtil = profileUtil,
                        visibilityContext = visibilityContext
                    )
                }
            }

            is ComposablePreferenceItem -> {
                // Handle custom composables
                item.content()
            }
        }
    }
}
