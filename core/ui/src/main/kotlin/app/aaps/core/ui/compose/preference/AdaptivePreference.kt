/*
 * Adaptive Preference Support for Jetpack Compose
 * Provides preference components that support PreferenceKey types with visibility/validation logic
 */

package app.aaps.core.ui.compose.preference

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import android.app.Activity
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.BooleanPreferenceKey
import app.aaps.core.keys.interfaces.DoublePreferenceKey
import app.aaps.core.keys.interfaces.IntPreferenceKey
import app.aaps.core.keys.interfaces.IntentPreferenceKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.StringPreferenceKey

/**
 * Data class holding visibility and enabled state for a preference
 */
data class PreferenceVisibility(
    val visible: Boolean,
    val enabled: Boolean
)

/**
 * Calculates visibility and enabled state for a preference based on mode settings and dependencies.
 */
fun calculatePreferenceVisibility(
    preferenceKey: PreferenceKey,
    preferences: Preferences,
    config: Config,
    engineeringModeOnly: Boolean = false
): PreferenceVisibility {
    var visible = true
    var enabled = true

    // Check simple mode
    if (preferences.simpleMode && preferenceKey.defaultedBySM) {
        visible = false
    }

    // Check APS mode
    if (preferences.apsMode && !preferenceKey.showInApsMode) {
        visible = false
        enabled = false
    }

    // Check NSClient mode
    if (preferences.nsclientMode && !preferenceKey.showInNsClientMode) {
        visible = false
        enabled = false
    }

    // Check PumpControl mode
    if (preferences.pumpControlMode && !preferenceKey.showInPumpControlMode) {
        visible = false
        enabled = false
    }

    // Check engineering mode
    if (!config.isEngineeringMode() && engineeringModeOnly) {
        visible = false
        enabled = false
    }

    // Check dependency
    preferenceKey.dependency?.let {
        if (!preferences.get(it)) {
            visible = false
        }
    }

    // Check negative dependency
    preferenceKey.negativeDependency?.let {
        if (preferences.get(it)) {
            visible = false
        }
    }

    return PreferenceVisibility(visible, enabled)
}

/**
 * Remembers a MutableState for a BooleanPreferenceKey backed by Preferences.
 */
@Composable
fun rememberPreferenceBooleanState(
    preferences: Preferences,
    key: BooleanPreferenceKey
): MutableState<Boolean> {
    return remember(key, preferences) {
        PreferenceBooleanState(preferences, key)
    }
}

/**
 * Remembers a MutableState for a StringPreferenceKey backed by Preferences.
 */
@Composable
fun rememberPreferenceStringState(
    preferences: Preferences,
    key: StringPreferenceKey
): MutableState<String> {
    return remember(key, preferences) {
        PreferenceStringState(preferences, key)
    }
}

/**
 * Remembers a MutableState for an IntPreferenceKey backed by Preferences.
 */
@Composable
fun rememberPreferenceIntState(
    preferences: Preferences,
    key: IntPreferenceKey
): MutableState<Int> {
    return remember(key, preferences) {
        PreferenceIntState(preferences, key)
    }
}

/**
 * Remembers a MutableState for a DoublePreferenceKey backed by Preferences.
 */
@Composable
fun rememberPreferenceDoubleState(
    preferences: Preferences,
    key: DoublePreferenceKey
): MutableState<Double> {
    return remember(key, preferences) {
        PreferenceDoubleState(preferences, key)
    }
}

@Stable
private class PreferenceBooleanState(
    private val preferences: Preferences,
    private val key: BooleanPreferenceKey
) : MutableState<Boolean> {

    private val state = mutableStateOf(preferences.get(key))

    override var value: Boolean
        get() = state.value
        set(value) {
            state.value = value
            preferences.put(key, value)
        }

    override fun component1(): Boolean = value
    override fun component2(): (Boolean) -> Unit = { value = it }
}

@Stable
private class PreferenceStringState(
    private val preferences: Preferences,
    private val key: StringPreferenceKey
) : MutableState<String> {

    private val state = mutableStateOf(preferences.get(key))

    override var value: String
        get() = state.value
        set(value) {
            state.value = value
            preferences.put(key, value)
        }

    override fun component1(): String = value
    override fun component2(): (String) -> Unit = { value = it }
}

@Stable
private class PreferenceIntState(
    private val preferences: Preferences,
    private val key: IntPreferenceKey
) : MutableState<Int> {

    private val state = mutableStateOf(preferences.get(key))

    override var value: Int
        get() = state.value
        set(value) {
            // Clamp to min/max
            val clampedValue = value.coerceIn(key.min, key.max)
            state.value = clampedValue
            preferences.put(key, clampedValue)
        }

    override fun component1(): Int = value
    override fun component2(): (Int) -> Unit = { value = it }
}

@Stable
private class PreferenceDoubleState(
    private val preferences: Preferences,
    private val key: DoublePreferenceKey
) : MutableState<Double> {

    private val state = mutableStateOf(preferences.get(key))

    override var value: Double
        get() = state.value
        set(value) {
            // Clamp to min/max
            val clampedValue = value.coerceIn(key.min, key.max)
            state.value = clampedValue
            preferences.put(key, clampedValue)
        }

    override fun component1(): Double = value
    override fun component2(): (Double) -> Unit = { value = it }
}

// =================================
// Adaptive Switch Preference
// =================================

/**
 * Adaptive switch preference that uses BooleanPreferenceKey directly.
 * Handles visibility based on mode settings and dependencies.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 */
fun LazyListScope.adaptiveSwitchPreference(
    preferences: Preferences,
    config: Config,
    booleanKey: BooleanPreferenceKey,
    titleResId: Int,
    summaryResId: Int? = null,
    summaryOnResId: Int? = null,
    summaryOffResId: Int? = null,
    keyPrefix: String = ""
) {
    val visibility = calculatePreferenceVisibility(
        preferenceKey = booleanKey,
        preferences = preferences,
        config = config,
        engineeringModeOnly = booleanKey.engineeringModeOnly
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${booleanKey.key}" else booleanKey.key
    item(key = itemKey, contentType = "AdaptiveSwitchPreference") {
        val state = rememberPreferenceBooleanState(preferences, booleanKey)
        SwitchPreference(
            state = state,
            title = { Text(stringResource(titleResId)) },
            summary = when {
                summaryOnResId != null && summaryOffResId != null -> {
                    { Text(stringResource(if (state.value) summaryOnResId else summaryOffResId)) }
                }

                summaryResId != null                              -> {
                    { Text(stringResource(summaryResId)) }
                }

                else                                              -> null
            },
            enabled = visibility.enabled
        )
    }
}

// =================================
// Adaptive Int Preference
// =================================

/**
 * Adaptive int preference that uses IntPreferenceKey directly.
 * Handles visibility based on mode settings and dependencies.
 * Validates input against min/max values.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 */
fun LazyListScope.adaptiveIntPreference(
    preferences: Preferences,
    config: Config,
    intKey: IntPreferenceKey,
    titleResId: Int,
    unit: String = "",
    showRange: Boolean = true,
    keyPrefix: String = ""
) {
    val visibility = calculatePreferenceVisibility(
        preferenceKey = intKey,
        preferences = preferences,
        config = config,
        engineeringModeOnly = intKey.engineeringModeOnly
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${intKey.key}" else intKey.key
    item(key = itemKey, contentType = "AdaptiveIntPreference") {
        val state = rememberPreferenceIntState(preferences, intKey)
        val value = state.value

        TextFieldPreference(
            state = state,
            title = { Text(stringResource(titleResId)) },
            textToValue = { text ->
                text.toIntOrNull()?.coerceIn(intKey.min, intKey.max)
            },
            enabled = visibility.enabled,
            summary = if (showRange) {
                { Text("$value$unit (${intKey.min}-${intKey.max})") }
            } else {
                { Text("$value$unit") }
            }
        )
    }
}

// =================================
// Adaptive Double Preference
// =================================

/**
 * Adaptive double preference that uses DoublePreferenceKey directly.
 * Handles visibility based on mode settings and dependencies.
 * Validates input against min/max values.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 */
fun LazyListScope.adaptiveDoublePreference(
    preferences: Preferences,
    config: Config,
    doubleKey: DoublePreferenceKey,
    titleResId: Int,
    unit: String = "",
    showRange: Boolean = true,
    keyPrefix: String = ""
) {
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
            title = { Text(stringResource(titleResId)) },
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

// =================================
// Adaptive String Preference
// =================================

/**
 * Adaptive string preference that uses StringPreferenceKey directly.
 * Handles visibility based on mode settings and dependencies.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 */
fun LazyListScope.adaptiveStringPreference(
    preferences: Preferences,
    config: Config,
    stringKey: StringPreferenceKey,
    titleResId: Int,
    summaryResId: Int? = null,
    isPassword: Boolean = false,
    keyPrefix: String = ""
) {
    val visibility = calculatePreferenceVisibility(
        preferenceKey = stringKey,
        preferences = preferences,
        config = config
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${stringKey.key}" else stringKey.key
    item(key = itemKey, contentType = "AdaptiveStringPreference") {
        val state = rememberPreferenceStringState(preferences, stringKey)
        val value = state.value

        TextFieldPreference(
            state = state,
            title = { Text(stringResource(titleResId)) },
            textToValue = { it },
            enabled = visibility.enabled,
            summary = when {
                isPassword || stringKey.isPassword -> {
                    { if (value.isNotEmpty()) Text("••••••••") else summaryResId?.let { Text(stringResource(it)) } }
                }

                value.isNotEmpty()                 -> {
                    { Text(value) }
                }

                summaryResId != null               -> {
                    { Text(stringResource(summaryResId)) }
                }

                else                               -> null
            }
        )
    }
}

// =================================
// Adaptive List Int Preference
// =================================

/**
 * Adaptive list int preference that uses IntPreferenceKey directly.
 * Shows a dialog with a list of options to choose from.
 * Handles visibility based on mode settings and dependencies.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 */
fun LazyListScope.adaptiveListIntPreference(
    preferences: Preferences,
    config: Config,
    intKey: IntPreferenceKey,
    titleResId: Int,
    entries: List<String>,
    entryValues: List<Int>,
    keyPrefix: String = ""
) {
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
            title = { Text(stringResource(titleResId)) },
            enabled = visibility.enabled,
            summary = { Text(currentEntry) },
            valueToText = { value ->
                val index = entryValues.indexOf(value)
                androidx.compose.ui.text.AnnotatedString(entries.getOrElse(index) { value.toString() })
            }
        )
    }
}

// =================================
// Adaptive String List Preference
// =================================

/**
 * Adaptive string list preference that uses StringPreferenceKey directly.
 * Shows a dialog with a list of options to choose from.
 * Handles visibility based on mode settings and dependencies.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 */
fun LazyListScope.adaptiveStringListPreference(
    preferences: Preferences,
    config: Config,
    stringKey: StringPreferenceKey,
    titleResId: Int,
    entries: Map<String, String>,
    keyPrefix: String = ""
) {
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
            title = { Text(stringResource(titleResId)) },
            enabled = visibility.enabled,
            summary = { Text(currentEntry) },
            valueToText = { value ->
                androidx.compose.ui.text.AnnotatedString(entries[value] ?: value)
            }
        )
    }
}

// =================================
// Adaptive Intent Preference
// =================================

/**
 * Calculates visibility and enabled state for an intent preference based on mode settings and dependencies.
 */
fun calculateIntentPreferenceVisibility(
    intentKey: IntentPreferenceKey,
    preferences: Preferences
): PreferenceVisibility {
    var visible = true
    var enabled = true

    // Check simple mode
    if (preferences.simpleMode && intentKey.defaultedBySM) {
        visible = false
    }

    // Check APS mode
    if (preferences.apsMode && !intentKey.showInApsMode) {
        visible = false
        enabled = false
    }

    // Check NSClient mode
    if (preferences.nsclientMode && !intentKey.showInNsClientMode) {
        visible = false
        enabled = false
    }

    // Check PumpControl mode
    if (preferences.pumpControlMode && !intentKey.showInPumpControlMode) {
        visible = false
        enabled = false
    }

    // Check dependency
    intentKey.dependency?.let {
        if (!preferences.get(it)) {
            visible = false
        }
    }

    // Check negative dependency
    intentKey.negativeDependency?.let {
        if (preferences.get(it)) {
            visible = false
        }
    }

    return PreferenceVisibility(visible, enabled)
}

/**
 * Adaptive intent preference that uses IntentPreferenceKey directly.
 * Handles visibility based on mode settings and dependencies.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 *
 * @param preferences The Preferences instance for visibility checks
 * @param intentKey The IntentPreferenceKey for this preference
 * @param titleResId Resource ID for the title string
 * @param summaryResId Optional resource ID for the summary string
 * @param onClick Callback invoked when the preference is clicked. Use this to launch intents/activities.
 * @param keyPrefix Optional prefix for the preference key
 */
fun LazyListScope.adaptiveIntentPreference(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int,
    summaryResId: Int? = null,
    onClick: () -> Unit,
    keyPrefix: String = ""
) {
    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${intentKey.key}" else intentKey.key
    item(key = itemKey, contentType = "AdaptiveIntentPreference") {
        Preference(
            title = { Text(stringResource(titleResId)) },
            summary = summaryResId?.let { { Text(stringResource(it)) } },
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
 * @param titleResId Resource ID for the title string
 * @param url The URL to open when clicked
 * @param keyPrefix Optional prefix for the preference key
 */
fun LazyListScope.adaptiveUrlPreference(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int,
    url: String,
    keyPrefix: String = ""
) {
    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${intentKey.key}" else intentKey.key
    item(key = itemKey, contentType = "AdaptiveUrlPreference") {
        val uriHandler = LocalUriHandler.current
        Preference(
            title = { Text(stringResource(titleResId)) },
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
 * @param titleResId Resource ID for the title string
 * @param activityClass The Activity class to launch when clicked
 * @param summaryResId Optional resource ID for the summary string
 * @param keyPrefix Optional prefix for the preference key
 */
fun <T : Activity> LazyListScope.adaptiveActivityPreference(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int,
    activityClass: Class<T>,
    summaryResId: Int? = null,
    keyPrefix: String = ""
) {
    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${intentKey.key}" else intentKey.key
    item(key = itemKey, contentType = "AdaptiveActivityPreference") {
        val context = LocalContext.current
        Preference(
            title = { Text(stringResource(titleResId)) },
            summary = summaryResId?.let { { Text(stringResource(it)) } },
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
 * @param titleResId Resource ID for the title string
 * @param activityClass The Activity class to launch when clicked (dynamic Class<*>)
 * @param summaryResId Optional resource ID for the summary string
 * @param keyPrefix Optional prefix for the preference key
 */
fun LazyListScope.adaptiveDynamicActivityPreference(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int,
    activityClass: Class<*>,
    summaryResId: Int? = null,
    keyPrefix: String = ""
) {
    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${intentKey.key}" else intentKey.key
    item(key = itemKey, contentType = "AdaptiveDynamicActivityPreference") {
        val context = LocalContext.current
        Preference(
            title = { Text(stringResource(titleResId)) },
            summary = summaryResId?.let { { Text(stringResource(it)) } },
            enabled = visibility.enabled,
            onClick = if (visibility.enabled) {
                { context.startActivity(Intent(context, activityClass)) }
            } else null
        )
    }
}

// =================================
// Composable Preference Versions (for use inside Card sections)
// =================================

/**
 * Composable switch preference for use inside card sections.
 */
@Composable
fun AdaptiveSwitchPreferenceItem(
    preferences: Preferences,
    config: Config,
    booleanKey: BooleanPreferenceKey,
    titleResId: Int,
    summaryResId: Int? = null,
    summaryOnResId: Int? = null,
    summaryOffResId: Int? = null
) {
    val visibility = calculatePreferenceVisibility(
        preferenceKey = booleanKey,
        preferences = preferences,
        config = config,
        engineeringModeOnly = booleanKey.engineeringModeOnly
    )

    if (!visibility.visible) return

    val state = rememberPreferenceBooleanState(preferences, booleanKey)
    SwitchPreference(
        state = state,
        title = { Text(stringResource(titleResId)) },
        summary = when {
            summaryOnResId != null && summaryOffResId != null -> {
                { Text(stringResource(if (state.value) summaryOnResId else summaryOffResId)) }
            }
            summaryResId != null -> {
                { Text(stringResource(summaryResId)) }
            }
            else -> null
        },
        enabled = visibility.enabled
    )
}

/**
 * Composable int preference for use inside card sections.
 */
@Composable
fun AdaptiveIntPreferenceItem(
    preferences: Preferences,
    config: Config,
    intKey: IntPreferenceKey,
    titleResId: Int,
    unit: String = "",
    showRange: Boolean = true
) {
    val visibility = calculatePreferenceVisibility(
        preferenceKey = intKey,
        preferences = preferences,
        config = config,
        engineeringModeOnly = intKey.engineeringModeOnly
    )

    if (!visibility.visible) return

    val state = rememberPreferenceIntState(preferences, intKey)
    val value = state.value

    TextFieldPreference(
        state = state,
        title = { Text(stringResource(titleResId)) },
        textToValue = { text ->
            text.toIntOrNull()?.coerceIn(intKey.min, intKey.max)
        },
        enabled = visibility.enabled,
        summary = if (showRange) {
            { Text("$value$unit (${intKey.min}-${intKey.max})") }
        } else {
            { Text("$value$unit") }
        }
    )
}

/**
 * Composable double preference for use inside card sections.
 */
@Composable
fun AdaptiveDoublePreferenceItem(
    preferences: Preferences,
    config: Config,
    doubleKey: DoublePreferenceKey,
    titleResId: Int,
    unit: String = "",
    showRange: Boolean = true
) {
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
        title = { Text(stringResource(titleResId)) },
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

/**
 * Composable string preference for use inside card sections.
 */
@Composable
fun AdaptiveStringPreferenceItem(
    preferences: Preferences,
    config: Config,
    stringKey: StringPreferenceKey,
    titleResId: Int,
    summaryResId: Int? = null,
    isPassword: Boolean = false
) {
    val visibility = calculatePreferenceVisibility(
        preferenceKey = stringKey,
        preferences = preferences,
        config = config
    )

    if (!visibility.visible) return

    val state = rememberPreferenceStringState(preferences, stringKey)
    val value = state.value

    TextFieldPreference(
        state = state,
        title = { Text(stringResource(titleResId)) },
        textToValue = { it },
        enabled = visibility.enabled,
        summary = when {
            isPassword || stringKey.isPassword -> {
                { if (value.isNotEmpty()) Text("••••••••") else summaryResId?.let { Text(stringResource(it)) } }
            }
            value.isNotEmpty() -> {
                { Text(value) }
            }
            summaryResId != null -> {
                { Text(stringResource(summaryResId)) }
            }
            else -> null
        }
    )
}

/**
 * Composable intent preference for use inside card sections.
 */
@Composable
fun AdaptiveIntentPreferenceItem(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int,
    summaryResId: Int? = null,
    onClick: () -> Unit
) {
    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    Preference(
        title = { Text(stringResource(titleResId)) },
        summary = summaryResId?.let { { Text(stringResource(it)) } },
        enabled = visibility.enabled,
        onClick = if (visibility.enabled) onClick else null
    )
}

/**
 * Composable URL preference for use inside card sections.
 */
@Composable
fun AdaptiveUrlPreferenceItem(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int,
    url: String
) {
    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val uriHandler = LocalUriHandler.current
    Preference(
        title = { Text(stringResource(titleResId)) },
        summary = { Text(url) },
        enabled = visibility.enabled,
        onClick = if (visibility.enabled) {
            { uriHandler.openUri(url) }
        } else null
    )
}

/**
 * Composable activity preference for use inside card sections.
 */
@Composable
fun <T : Activity> AdaptiveActivityPreferenceItem(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int,
    activityClass: Class<T>,
    summaryResId: Int? = null
) {
    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val context = LocalContext.current
    Preference(
        title = { Text(stringResource(titleResId)) },
        summary = summaryResId?.let { { Text(stringResource(it)) } },
        enabled = visibility.enabled,
        onClick = if (visibility.enabled) {
            { context.startActivity(Intent(context, activityClass)) }
        } else null
    )
}

/**
 * Composable dynamic activity preference for use inside card sections.
 */
@Composable
fun AdaptiveDynamicActivityPreferenceItem(
    preferences: Preferences,
    intentKey: IntentPreferenceKey,
    titleResId: Int,
    activityClass: Class<*>,
    summaryResId: Int? = null
) {
    val visibility = calculateIntentPreferenceVisibility(
        intentKey = intentKey,
        preferences = preferences
    )

    if (!visibility.visible) return

    val context = LocalContext.current
    Preference(
        title = { Text(stringResource(titleResId)) },
        summary = summaryResId?.let { { Text(stringResource(it)) } },
        enabled = visibility.enabled,
        onClick = if (visibility.enabled) {
            { context.startActivity(Intent(context, activityClass)) }
        } else null
    )
}

/**
 * Composable list int preference for use inside card sections.
 */
@Composable
fun AdaptiveListIntPreferenceItem(
    preferences: Preferences,
    config: Config,
    intKey: IntPreferenceKey,
    titleResId: Int,
    entries: List<String>,
    entryValues: List<Int>
) {
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
        title = { Text(stringResource(titleResId)) },
        enabled = visibility.enabled,
        summary = { Text(currentEntry) },
        valueToText = { value ->
            val index = entryValues.indexOf(value)
            androidx.compose.ui.text.AnnotatedString(entries.getOrElse(index) { value.toString() })
        }
    )
}

/**
 * Composable string list preference for use inside card sections.
 */
@Composable
fun AdaptiveStringListPreferenceItem(
    preferences: Preferences,
    config: Config,
    stringKey: StringPreferenceKey,
    titleResId: Int,
    entries: Map<String, String>
) {
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
        title = { Text(stringResource(titleResId)) },
        enabled = visibility.enabled,
        summary = { Text(currentEntry) },
        valueToText = { value ->
            androidx.compose.ui.text.AnnotatedString(entries[value] ?: value)
        }
    )
}
