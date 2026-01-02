/*
 * Adaptive List Preferences for Jetpack Compose
 */

package app.aaps.core.ui.compose.preference

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.IntPreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.StringPreferenceKey

// =================================
// Adaptive List Int Preference
// =================================

/**
 * Adaptive list int preference that uses IntPreferenceKey directly.
 * Shows a dialog with a list of options to choose from.
 * Handles visibility based on mode settings and dependencies.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intKey.titleResId
 */
fun LazyListScope.adaptiveListIntPreference(
    preferences: Preferences,
    config: Config,
    intKey: IntPreferenceKey,
    titleResId: Int = 0,
    entries: List<String>,
    entryValues: List<Int>,
    keyPrefix: String = ""
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculatePreferenceVisibility(
        preferenceKey = intKey,
        preferences = preferences,
        config = config,
        engineeringModeOnly = intKey.engineeringModeOnly
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${intKey.key}" else intKey.key
    item(key = itemKey, contentType = "AdaptiveListIntPreference") {
        val state = rememberPreferenceIntState(preferences, intKey)
        val currentValue = state.value
        val currentIndex = entryValues.indexOf(currentValue).coerceAtLeast(0)
        val currentEntry = entries.getOrElse(currentIndex) { currentValue.toString() }

        ListPreference(
            state = state,
            values = entryValues,
            title = { Text(stringResource(effectiveTitleResId)) },
            enabled = visibility.enabled,
            summary = { Text(currentEntry) },
            valueToText = { value ->
                val index = entryValues.indexOf(value)
                AnnotatedString(entries.getOrElse(index) { value.toString() })
            }
        )
    }
}

/**
 * Composable list int preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intKey.titleResId
 */
@Composable
fun AdaptiveListIntPreferenceItem(
    preferences: Preferences,
    config: Config,
    intKey: IntPreferenceKey,
    titleResId: Int = 0,
    entries: List<String>,
    entryValues: List<Int>
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculatePreferenceVisibility(
        preferenceKey = intKey,
        preferences = preferences,
        config = config,
        engineeringModeOnly = intKey.engineeringModeOnly
    )

    if (!visibility.visible) return

    val state = rememberPreferenceIntState(preferences, intKey)
    val currentValue = state.value
    val currentIndex = entryValues.indexOf(currentValue).coerceAtLeast(0)
    val currentEntry = entries.getOrElse(currentIndex) { currentValue.toString() }

    ListPreference(
        state = state,
        values = entryValues,
        title = { Text(stringResource(effectiveTitleResId)) },
        enabled = visibility.enabled,
        summary = { Text(currentEntry) },
        valueToText = { value ->
            val index = entryValues.indexOf(value)
            AnnotatedString(entries.getOrElse(index) { value.toString() })
        }
    )
}

// =================================
// Adaptive String List Preference
// =================================

/**
 * Adaptive string list preference that uses StringPreferenceKey directly.
 * Shows a dialog with a list of options to choose from.
 * Handles visibility based on mode settings and dependencies.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses stringKey.titleResId
 */
fun LazyListScope.adaptiveStringListPreference(
    preferences: Preferences,
    config: Config,
    stringKey: StringPreferenceKey,
    titleResId: Int = 0,
    entries: Map<String, String>,
    keyPrefix: String = ""
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else stringKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculatePreferenceVisibility(
        preferenceKey = stringKey,
        preferences = preferences,
        config = config
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${stringKey.key}" else stringKey.key
    item(key = itemKey, contentType = "AdaptiveStringListPreference") {
        val state = rememberPreferenceStringState(preferences, stringKey)
        val currentValue = state.value
        val currentEntry = entries[currentValue] ?: currentValue
        val values = entries.keys.toList()

        ListPreference(
            state = state,
            values = values,
            title = { Text(stringResource(effectiveTitleResId)) },
            enabled = visibility.enabled,
            summary = { Text(currentEntry) },
            valueToText = { value ->
                AnnotatedString(entries[value] ?: value)
            }
        )
    }
}

/**
 * Composable string list preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses stringKey.titleResId
 */
@Composable
fun AdaptiveStringListPreferenceItem(
    preferences: Preferences,
    config: Config,
    stringKey: StringPreferenceKey,
    titleResId: Int = 0,
    entries: Map<String, String>
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else stringKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculatePreferenceVisibility(
        preferenceKey = stringKey,
        preferences = preferences,
        config = config
    )

    if (!visibility.visible) return

    val state = rememberPreferenceStringState(preferences, stringKey)
    val currentValue = state.value
    val currentEntry = entries[currentValue] ?: currentValue
    val values = entries.keys.toList()

    ListPreference(
        state = state,
        values = values,
        title = { Text(stringResource(effectiveTitleResId)) },
        enabled = visibility.enabled,
        summary = { Text(currentEntry) },
        valueToText = { value ->
            AnnotatedString(entries[value] ?: value)
        }
    )
}
