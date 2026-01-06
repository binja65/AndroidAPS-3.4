/*
 * Adaptive Int Preference for Jetpack Compose
 */

package app.aaps.core.ui.compose.preference

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.IntPreferenceKey
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.Preferences

/**
 * Composable int preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intKey.titleResId
 * @param visibilityContext Optional context for evaluating runtime visibility/enabled conditions
 */
@Composable
fun AdaptiveIntPreferenceItem(
    preferences: Preferences,
    config: Config,
    intKey: IntPreferenceKey,
    titleResId: Int = 0,
    unit: String = "",
    showRange: Boolean = true,
    visibilityContext: PreferenceVisibilityContext? = null
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculatePreferenceVisibility(
        preferenceKey = intKey,
        preferences = preferences,
        config = config,
        engineeringModeOnly = intKey.engineeringModeOnly,
        visibilityContext = visibilityContext
    )

    if (!visibility.visible) return

    val state = rememberPreferenceIntState(preferences, intKey)
    val value = state.value
    // Only show range if both min and max are meaningful values
    val hasValidRange = intKey.min != Int.MIN_VALUE && intKey.max != Int.MAX_VALUE

    TextFieldPreference(
        state = state,
        title = { Text(stringResource(effectiveTitleResId)) },
        textToValue = { text ->
            text.toIntOrNull()?.coerceIn(intKey.min, intKey.max)
        },
        enabled = visibility.enabled,
        summary = {
            val unitsResId = intKey.unitsResId
            val summaryText = when {
                unitsResId != null -> {
                    // Use formatted string resource with units
                    stringResource(unitsResId, value, intKey.min, intKey.max)
                }
                showRange && hasValidRange -> {
                    // Fallback to old behavior with unit parameter
                    "$value$unit (${intKey.min} - ${intKey.max})"
                }
                else -> {
                    "$value$unit"
                }
            }
            Text(summaryText)
        }
    )
}
