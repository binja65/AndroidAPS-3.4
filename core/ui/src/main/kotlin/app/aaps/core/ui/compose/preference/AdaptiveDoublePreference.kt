/*
 * Adaptive Double Preference for Jetpack Compose
 */

package app.aaps.core.ui.compose.preference

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.DoublePreferenceKey
import app.aaps.core.keys.interfaces.Preferences

/**
 * Adaptive double preference that uses DoublePreferenceKey directly.
 * Handles visibility based on mode settings and dependencies.
 * Validates input against min/max values.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses doubleKey.titleResId
 */
fun LazyListScope.adaptiveDoublePreference(
    preferences: Preferences,
    config: Config,
    doubleKey: DoublePreferenceKey,
    titleResId: Int = 0,
    unit: String = "",
    showRange: Boolean = true,
    keyPrefix: String = ""
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else doubleKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculatePreferenceVisibility(
        preferenceKey = doubleKey,
        preferences = preferences,
        config = config
    )

    // Also check calculatedBySM for doubles
    if (!visibility.visible || (preferences.simpleMode && doubleKey.calculatedBySM)) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${doubleKey.key}" else doubleKey.key
    item(key = itemKey, contentType = "AdaptiveDoublePreference") {
        val state = rememberPreferenceDoubleState(preferences, doubleKey)
        val value = state.value

        TextFieldPreference(
            state = state,
            title = { Text(stringResource(effectiveTitleResId)) },
            textToValue = { text ->
                text.toDoubleOrNull()?.coerceIn(doubleKey.min, doubleKey.max)
            },
            enabled = visibility.enabled,
            summary = if (showRange) {
                { Text("$value$unit (${doubleKey.min}-${doubleKey.max})") }
            } else {
                { Text("$value$unit") }
            }
        )
    }
}

/**
 * Composable double preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses doubleKey.titleResId
 */
@Composable
fun AdaptiveDoublePreferenceItem(
    preferences: Preferences,
    config: Config,
    doubleKey: DoublePreferenceKey,
    titleResId: Int = 0,
    unit: String = "",
    showRange: Boolean = true
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else doubleKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculatePreferenceVisibility(
        preferenceKey = doubleKey,
        preferences = preferences,
        config = config
    )

    if (!visibility.visible || (preferences.simpleMode && doubleKey.calculatedBySM)) return

    val state = rememberPreferenceDoubleState(preferences, doubleKey)
    val value = state.value

    TextFieldPreference(
        state = state,
        title = { Text(stringResource(effectiveTitleResId)) },
        textToValue = { text ->
            text.toDoubleOrNull()?.coerceIn(doubleKey.min, doubleKey.max)
        },
        enabled = visibility.enabled,
        summary = if (showRange) {
            { Text("$value$unit (${doubleKey.min}-${doubleKey.max})") }
        } else {
            { Text("$value$unit") }
        }
    )
}
