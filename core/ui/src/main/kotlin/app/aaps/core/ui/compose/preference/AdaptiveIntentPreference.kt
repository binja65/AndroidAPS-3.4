/*
 * Adaptive Intent Preferences for Jetpack Compose
 */

package app.aaps.core.ui.compose.preference

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import app.aaps.core.keys.interfaces.IntentPreferenceKey
import app.aaps.core.keys.interfaces.Preferences

// =================================
// Adaptive Intent Preference
// =================================

/**
 * Adaptive intent preference that uses IntentPreferenceKey directly.
 * Handles visibility based on mode settings and dependencies.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 *
 * @param preferences The Preferences instance for visibility checks
 * @param intentKey The IntentPreferenceKey for this preference
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intentKey.titleResId
 * @param summaryResId Optional summary resource ID. If null, uses intentKey.summaryResId
 * @param onClick Callback invoked when the preference is clicked. Use this to launch intents/activities.
 * @param keyPrefix Optional prefix for the preference key
 */
fun LazyListScope.adaptiveIntentPreference(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int = 0,
    summaryResId: Int? = null,
    onClick: () -> Unit,
    keyPrefix: String = ""
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intentKey.titleResId
    val effectiveSummaryResId = summaryResId ?: intentKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${intentKey.key}" else intentKey.key
    item(key = itemKey, contentType = "AdaptiveIntentPreference") {
        Preference(
            title = { Text(stringResource(effectiveTitleResId)) },
            summary = effectiveSummaryResId?.let { { Text(stringResource(it)) } },
            enabled = visibility.enabled,
            onClick = if (visibility.enabled) onClick else null
        )
    }
}

/**
 * Adaptive intent preference that opens a URL in browser.
 * Uses Compose's LocalUriHandler to open URLs without requiring Context.
 *
 * @param preferences The Preferences instance for visibility checks
 * @param intentKey The IntentPreferenceKey for this preference
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intentKey.titleResId
 * @param url The URL to open when clicked
 * @param keyPrefix Optional prefix for the preference key
 */
fun LazyListScope.adaptiveUrlPreference(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int = 0,
    url: String,
    keyPrefix: String = ""
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intentKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${intentKey.key}" else intentKey.key
    item(key = itemKey, contentType = "AdaptiveUrlPreference") {
        val uriHandler = LocalUriHandler.current
        Preference(
            title = { Text(stringResource(effectiveTitleResId)) },
            summary = { Text(url) },
            enabled = visibility.enabled,
            onClick = if (visibility.enabled) {
                { uriHandler.openUri(url) }
            } else null
        )
    }
}

/**
 * Adaptive intent preference that launches an Activity.
 * Uses LocalContext to start the activity without requiring Context in constructor.
 *
 * @param preferences The Preferences instance for visibility checks
 * @param intentKey The IntentPreferenceKey for this preference
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intentKey.titleResId
 * @param activityClass The Activity class to launch when clicked
 * @param summaryResId Optional summary resource ID. If null, uses intentKey.summaryResId
 * @param keyPrefix Optional prefix for the preference key
 */
fun <T : Activity> LazyListScope.adaptiveActivityPreference(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int = 0,
    activityClass: Class<T>,
    summaryResId: Int? = null,
    keyPrefix: String = ""
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intentKey.titleResId
    val effectiveSummaryResId = summaryResId ?: intentKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${intentKey.key}" else intentKey.key
    item(key = itemKey, contentType = "AdaptiveActivityPreference") {
        val context = LocalContext.current
        Preference(
            title = { Text(stringResource(effectiveTitleResId)) },
            summary = effectiveSummaryResId?.let { { Text(stringResource(it)) } },
            enabled = visibility.enabled,
            onClick = if (visibility.enabled) {
                { context.startActivity(Intent(context, activityClass)) }
            } else null
        )
    }
}

/**
 * Adaptive intent preference that launches an Activity using Class<*>.
 * Useful when the activity class is dynamically provided (e.g., from UiInteraction).
 *
 * @param preferences The Preferences instance for visibility checks
 * @param intentKey The IntentPreferenceKey for this preference
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intentKey.titleResId
 * @param activityClass The Activity class to launch when clicked (dynamic Class<*>)
 * @param summaryResId Optional summary resource ID. If null, uses intentKey.summaryResId
 * @param keyPrefix Optional prefix for the preference key
 */
fun LazyListScope.adaptiveDynamicActivityPreference(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int = 0,
    activityClass: Class<*>,
    summaryResId: Int? = null,
    keyPrefix: String = ""
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intentKey.titleResId
    val effectiveSummaryResId = summaryResId ?: intentKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${intentKey.key}" else intentKey.key
    item(key = itemKey, contentType = "AdaptiveDynamicActivityPreference") {
        val context = LocalContext.current
        Preference(
            title = { Text(stringResource(effectiveTitleResId)) },
            summary = effectiveSummaryResId?.let { { Text(stringResource(it)) } },
            enabled = visibility.enabled,
            onClick = if (visibility.enabled) {
                { context.startActivity(Intent(context, activityClass)) }
            } else null
        )
    }
}

// =================================
// Composable Versions (for use inside Card sections)
// =================================

/**
 * Composable intent preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intentKey.titleResId
 * @param summaryResId Optional summary resource ID. If null, uses intentKey.summaryResId
 */
@Composable
fun AdaptiveIntentPreferenceItem(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int = 0,
    summaryResId: Int? = null,
    onClick: () -> Unit
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intentKey.titleResId
    val effectiveSummaryResId = summaryResId ?: intentKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    Preference(
        title = { Text(stringResource(effectiveTitleResId)) },
        summary = effectiveSummaryResId?.let { { Text(stringResource(it)) } },
        enabled = visibility.enabled,
        onClick = if (visibility.enabled) onClick else null
    )
}

/**
 * Composable URL preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intentKey.titleResId
 */
@Composable
fun AdaptiveUrlPreferenceItem(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int = 0,
    url: String
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intentKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val uriHandler = LocalUriHandler.current
    Preference(
        title = { Text(stringResource(effectiveTitleResId)) },
        summary = { Text(url) },
        enabled = visibility.enabled,
        onClick = if (visibility.enabled) {
            { uriHandler.openUri(url) }
        } else null
    )
}

/**
 * Composable activity preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intentKey.titleResId
 * @param summaryResId Optional summary resource ID. If null, uses intentKey.summaryResId
 */
@Composable
fun <T : Activity> AdaptiveActivityPreferenceItem(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int = 0,
    activityClass: Class<T>,
    summaryResId: Int? = null
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intentKey.titleResId
    val effectiveSummaryResId = summaryResId ?: intentKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val context = LocalContext.current
    Preference(
        title = { Text(stringResource(effectiveTitleResId)) },
        summary = effectiveSummaryResId?.let { { Text(stringResource(it)) } },
        enabled = visibility.enabled,
        onClick = if (visibility.enabled) {
            { context.startActivity(Intent(context, activityClass)) }
        } else null
    )
}

/**
 * Composable activity preference that uses activityClass from the key.
 * Use this when the IntentPreferenceKey has activityClass defined.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intentKey.titleResId
 * @param summaryResId Optional summary resource ID. If null, uses intentKey.summaryResId
 */
@Composable
fun AdaptiveActivityPreferenceItem(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int = 0,
    summaryResId: Int? = null
) {
    val activityClass = intentKey.activityClass
        ?: return // Skip if no activityClass defined in key

    val effectiveTitleResId = if (titleResId != 0) titleResId else intentKey.titleResId
    val effectiveSummaryResId = summaryResId ?: intentKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val context = LocalContext.current
    Preference(
        title = { Text(stringResource(effectiveTitleResId)) },
        summary = effectiveSummaryResId?.let { { Text(stringResource(it)) } },
        enabled = visibility.enabled,
        onClick = if (visibility.enabled) {
            { context.startActivity(Intent(context, activityClass)) }
        } else null
    )
}

/**
 * Composable dynamic activity preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intentKey.titleResId
 * @param summaryResId Optional summary resource ID. If null, uses intentKey.summaryResId
 */
@Composable
fun AdaptiveDynamicActivityPreferenceItem(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int = 0,
    activityClass: Class<*>,
    summaryResId: Int? = null
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else intentKey.titleResId
    val effectiveSummaryResId = summaryResId ?: intentKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val context = LocalContext.current
    Preference(
        title = { Text(stringResource(effectiveTitleResId)) },
        summary = effectiveSummaryResId?.let { { Text(stringResource(it)) } },
        enabled = visibility.enabled,
        onClick = if (visibility.enabled) {
            { context.startActivity(Intent(context, activityClass)) }
        } else null
    )
}
