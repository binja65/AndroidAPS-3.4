/*
 * Adaptive Double Preference for Jetpack Compose
 */

package app.aaps.core.ui.compose.preference

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.DoublePreferenceKey
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.Preferences

/**
 * Composable double preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses doubleKey.titleResId
 * @param visibilityContext Optional context for evaluating runtime visibility/enabled conditions
 */
@Composable
fun AdaptiveDoublePreferenceItem(
    preferences: Preferences,
    config: Config,
    doubleKey: DoublePreferenceKey,
    titleResId: Int = 0,
    unit: String = "",
    showRange: Boolean = true,
    visibilityContext: PreferenceVisibilityContext? = null
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else doubleKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculatePreferenceVisibility(
        preferenceKey = doubleKey,
        preferences = preferences,
        config = config,
        visibilityContext = visibilityContext
    )

    if (!visibility.visible || (preferences.simpleMode && doubleKey.calculatedBySM)) return

    val state = rememberPreferenceDoubleState(preferences, doubleKey)
    val value = state.value
    // Only show range if both min and max are meaningful values
    val hasValidRange = doubleKey.min != Double.MIN_VALUE && doubleKey.max != Double.MAX_VALUE

    TextFieldPreference(
        state = state,
        title = { Text(stringResource(effectiveTitleResId)) },
        textToValue = { text ->
            text.toDoubleOrNull()?.coerceIn(doubleKey.min, doubleKey.max)
        },
        enabled = visibility.enabled,
        summary = if (showRange && hasValidRange) {
            { Text("$value$unit (${doubleKey.min}-${doubleKey.max})") }
        } else {
            { Text("$value$unit") }
        }
    )
}
