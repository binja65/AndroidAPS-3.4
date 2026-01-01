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
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.PreferenceType
import app.aaps.core.keys.interfaces.BooleanPreferenceKey
import app.aaps.core.keys.interfaces.DoublePreferenceKey
import app.aaps.core.keys.interfaces.IntPreferenceKey
import app.aaps.core.keys.interfaces.IntentPreferenceKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.StringPreferenceKey
import app.aaps.core.keys.interfaces.UnitDoublePreferenceKey
import java.math.BigDecimal
import java.math.RoundingMode

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
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses booleanKey.titleResId
 * @param summaryResId Optional summary resource ID. If null, uses booleanKey.summaryResId
 */
fun LazyListScope.adaptiveSwitchPreference(
    preferences: Preferences,
    config: Config,
    booleanKey: BooleanPreferenceKey,
    titleResId: Int = 0,
    summaryResId: Int? = null,
    summaryOnResId: Int? = null,
    summaryOffResId: Int? = null,
    keyPrefix: String = ""
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else booleanKey.titleResId
    val effectiveSummaryResId = summaryResId ?: booleanKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

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
            title = { Text(stringResource(effectiveTitleResId)) },
            summary = when {
                summaryOnResId != null && summaryOffResId != null -> {
                    { Text(stringResource(if (state.value) summaryOnResId else summaryOffResId)) }
                }

                effectiveSummaryResId != null                     -> {
                    { Text(stringResource(effectiveSummaryResId)) }
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
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intKey.titleResId
 */
fun LazyListScope.adaptiveIntPreference(
    preferences: Preferences,
    config: Config,
    intKey: IntPreferenceKey,
    titleResId: Int = 0,
    unit: String = "",
    showRange: Boolean = true,
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
    item(key = itemKey, contentType = "AdaptiveIntPreference") {
        val state = rememberPreferenceIntState(preferences, intKey)
        val value = state.value

        TextFieldPreference(
            state = state,
            title = { Text(stringResource(effectiveTitleResId)) },
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

// =================================
// Adaptive String Preference
// =================================

/**
 * Adaptive string preference that uses StringPreferenceKey directly.
 * Handles visibility based on mode settings and dependencies.
 * Uses resource IDs to avoid cross-module Compose compiler issues.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses stringKey.titleResId
 * @param summaryResId Optional summary resource ID. If null, uses stringKey.summaryResId
 */
fun LazyListScope.adaptiveStringPreference(
    preferences: Preferences,
    config: Config,
    stringKey: StringPreferenceKey,
    titleResId: Int = 0,
    summaryResId: Int? = null,
    isPassword: Boolean = false,
    keyPrefix: String = ""
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else stringKey.titleResId
    val effectiveSummaryResId = summaryResId ?: stringKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

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
            title = { Text(stringResource(effectiveTitleResId)) },
            textToValue = { it },
            enabled = visibility.enabled,
            summary = when {
                isPassword || stringKey.isPassword -> {
                    { if (value.isNotEmpty()) Text("••••••••") else effectiveSummaryResId?.let { Text(stringResource(it)) } }
                }

                value.isNotEmpty()                 -> {
                    { Text(value) }
                }

                effectiveSummaryResId != null      -> {
                    { Text(stringResource(effectiveSummaryResId)) }
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
// Composable Preference Versions (for use inside Card sections)
// =================================

/**
 * Composable switch preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses booleanKey.titleResId
 * @param summaryResId Optional summary resource ID. If null, uses booleanKey.summaryResId
 */
@Composable
fun AdaptiveSwitchPreferenceItem(
    preferences: Preferences,
    config: Config,
    booleanKey: BooleanPreferenceKey,
    titleResId: Int = 0,
    summaryResId: Int? = null,
    summaryOnResId: Int? = null,
    summaryOffResId: Int? = null
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else booleanKey.titleResId
    val effectiveSummaryResId = summaryResId ?: booleanKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

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
        title = { Text(stringResource(effectiveTitleResId)) },
        summary = when {
            summaryOnResId != null && summaryOffResId != null -> {
                { Text(stringResource(if (state.value) summaryOnResId else summaryOffResId)) }
            }
            effectiveSummaryResId != null -> {
                { Text(stringResource(effectiveSummaryResId)) }
            }
            else -> null
        },
        enabled = visibility.enabled
    )
}

/**
 * Composable int preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses intKey.titleResId
 */
@Composable
fun AdaptiveIntPreferenceItem(
    preferences: Preferences,
    config: Config,
    intKey: IntPreferenceKey,
    titleResId: Int = 0,
    unit: String = "",
    showRange: Boolean = true
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
    val value = state.value

    TextFieldPreference(
        state = state,
        title = { Text(stringResource(effectiveTitleResId)) },
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

/**
 * Composable string preference for use inside card sections.
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses stringKey.titleResId
 * @param summaryResId Optional summary resource ID. If null, uses stringKey.summaryResId
 */
@Composable
fun AdaptiveStringPreferenceItem(
    preferences: Preferences,
    config: Config,
    stringKey: StringPreferenceKey,
    titleResId: Int = 0,
    summaryResId: Int? = null,
    isPassword: Boolean = false
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else stringKey.titleResId
    val effectiveSummaryResId = summaryResId ?: stringKey.summaryResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

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
        title = { Text(stringResource(effectiveTitleResId)) },
        textToValue = { it },
        enabled = visibility.enabled,
        summary = when {
            isPassword || stringKey.isPassword -> {
                { if (value.isNotEmpty()) Text("••••••••") else effectiveSummaryResId?.let { Text(stringResource(it)) } }
            }
            value.isNotEmpty() -> {
                { Text(value) }
            }
            effectiveSummaryResId != null -> {
                { Text(stringResource(effectiveSummaryResId)) }
            }
            else -> null
        }
    )
}

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
            androidx.compose.ui.text.AnnotatedString(entries.getOrElse(index) { value.toString() })
        }
    )
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
            androidx.compose.ui.text.AnnotatedString(entries[value] ?: value)
        }
    )
}

/**
 * State wrapper for unit double preferences that handles unit conversion.
 */
@Stable
class UnitDoublePreferenceState(
    private val preferences: Preferences,
    private val profileUtil: ProfileUtil,
    private val key: UnitDoublePreferenceKey,
    private val _displayValue: MutableState<String>
) {
    val displayValue: String
        get() = _displayValue.value

    fun updateDisplayValue(newValue: String) {
        _displayValue.value = newValue
        // Convert from display units back to mg/dL for storage
        val displayDouble = newValue.toDoubleOrNull() ?: return
        val mgdlValue = profileUtil.convertToMgdlDetect(displayDouble)
        preferences.put(key, mgdlValue)
    }
}

@Composable
fun rememberUnitDoublePreferenceState(
    preferences: Preferences,
    profileUtil: ProfileUtil,
    key: UnitDoublePreferenceKey
): UnitDoublePreferenceState {
    // Get stored value (in mg/dL) and convert to display units
    val storedValue = preferences.get(key)
    val displayValue = profileUtil.valueInCurrentUnitsDetect(storedValue)
    // Check if using mg/dL by comparing converted values (mg/dL stays the same, mmol/L gets divided)
    val isMgdl = displayValue == storedValue || (storedValue > 0 && displayValue / storedValue > 0.9)
    val precision = if (isMgdl) 0 else 1
    val formatted = BigDecimal(displayValue).setScale(precision, RoundingMode.HALF_UP).toPlainString()

    val displayState = remember { mutableStateOf(formatted) }

    return remember(key) {
        UnitDoublePreferenceState(preferences, profileUtil, key, displayState)
    }
}

/**
 * Composable unit double preference for use inside card sections.
 * Handles glucose unit conversion (mg/dL <-> mmol/L).
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses unitKey.titleResId
 */
@Composable
fun AdaptiveUnitDoublePreferenceItem(
    preferences: Preferences,
    config: Config,
    profileUtil: ProfileUtil,
    unitKey: UnitDoublePreferenceKey,
    titleResId: Int = 0
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else unitKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculatePreferenceVisibility(
        preferenceKey = unitKey,
        preferences = preferences,
        config = config
    )

    if (!visibility.visible || (preferences.simpleMode && unitKey.defaultedBySM)) return

    val state = rememberUnitDoublePreferenceState(preferences, profileUtil, unitKey)
    val minDisplay = profileUtil.valueInCurrentUnitsDetect(unitKey.minMgdl.toDouble())
    val maxDisplay = profileUtil.valueInCurrentUnitsDetect(unitKey.maxMgdl.toDouble())
    // Check if using mg/dL by comparing converted values
    val isMgdl = minDisplay == unitKey.minMgdl.toDouble()
    val precision = if (isMgdl) 0 else 1
    val unit = if (isMgdl) "mg/dl" else "mmol/L"
    val minFormatted = BigDecimal(minDisplay).setScale(precision, RoundingMode.HALF_UP).toPlainString()
    val maxFormatted = BigDecimal(maxDisplay).setScale(precision, RoundingMode.HALF_UP).toPlainString()

    val textState = remember { mutableStateOf(state.displayValue) }

    TextFieldPreference(
        state = textState,
        title = { Text(stringResource(effectiveTitleResId)) },
        textToValue = { text ->
            val value = text.toDoubleOrNull()
            if (value != null && value >= minDisplay && value <= maxDisplay) {
                state.updateDisplayValue(text)
                text
            } else {
                null
            }
        },
        enabled = visibility.enabled,
        summary = { Text("${state.displayValue} $unit ($minFormatted-$maxFormatted)") }
    )
}

// =================================
// Generic Preference Renderer
// =================================

/**
 * Renders a preference based on its PreferenceKey type and preferenceType.
 * Automatically selects the appropriate composable.
 *
 * For LIST types, loads entries from resources using entriesResId/entryValuesResId.
 * For URL/ACTIVITY types on IntentPreferenceKey, requires additional parameters.
 *
 * @param key The PreferenceKey to render
 * @param preferences The Preferences instance
 * @param config The Config instance
 * @param profileUtil Required for UnitDoublePreferenceKey
 * @param onIntentClick Optional click handler for IntentPreferenceKey with CLICK type
 * @param intentUrl Optional URL for IntentPreferenceKey with URL type
 * @param intentActivityClass Optional Activity class for IntentPreferenceKey with ACTIVITY type
 */
@Composable
fun AdaptivePreferenceItem(
    key: PreferenceKey,
    preferences: Preferences,
    config: Config,
    profileUtil: ProfileUtil? = null,
    onIntentClick: (() -> Unit)? = null,
    intentUrl: String? = null,
    intentActivityClass: Class<*>? = null
) {
    when (key) {
        is BooleanPreferenceKey -> {
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = key
            )
        }

        is IntPreferenceKey      -> {
            when (key.preferenceType) {
                PreferenceType.LIST       -> {
                    if (key.entries.isNotEmpty()) {
                        val entryValues = key.entries.keys.toList()
                        val entries = key.entries.values.map { stringResource(it) }
                        AdaptiveListIntPreferenceItem(
                            preferences = preferences,
                            config = config,
                            intKey = key,
                            entries = entries,
                            entryValues = entryValues
                        )
                    }
                }

                PreferenceType.TEXT_FIELD -> {
                    AdaptiveIntPreferenceItem(
                        preferences = preferences,
                        config = config,
                        intKey = key
                    )
                }

                else                      -> {
                    // Default to text field for unsupported types
                    AdaptiveIntPreferenceItem(
                        preferences = preferences,
                        config = config,
                        intKey = key
                    )
                }
            }
        }

        is DoublePreferenceKey   -> {
            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = key
            )
        }

        is StringPreferenceKey   -> {
            when (key.preferenceType) {
                PreferenceType.LIST       -> {
                    if (key.entries.isNotEmpty()) {
                        // Convert Map<String, Int> (value -> labelResId) to Map<String, String> (value -> label)
                        val entriesMap = key.entries.mapValues { (_, resId) -> stringResource(resId) }
                        AdaptiveStringListPreferenceItem(
                            preferences = preferences,
                            config = config,
                            stringKey = key,
                            entries = entriesMap
                        )
                    }
                }

                PreferenceType.TEXT_FIELD -> {
                    AdaptiveStringPreferenceItem(
                        preferences = preferences,
                        config = config,
                        stringKey = key
                    )
                }

                else                      -> {
                    AdaptiveStringPreferenceItem(
                        preferences = preferences,
                        config = config,
                        stringKey = key
                    )
                }
            }
        }

        is UnitDoublePreferenceKey -> {
            profileUtil?.let {
                AdaptiveUnitDoublePreferenceItem(
                    preferences = preferences,
                    config = config,
                    profileUtil = it,
                    unitKey = key
                )
            }
        }

        is IntentPreferenceKey   -> {
            when (key.preferenceType) {
                PreferenceType.URL      -> {
                    intentUrl?.let { url ->
                        AdaptiveUrlPreferenceItem(
                            preferences = preferences,
                            intentKey = key,
                            url = url
                        )
                    }
                }

                PreferenceType.ACTIVITY -> {
                    intentActivityClass?.let { activityClass ->
                        AdaptiveDynamicActivityPreferenceItem(
                            preferences = preferences,
                            intentKey = key,
                            activityClass = activityClass
                        )
                    }
                }

                PreferenceType.CLICK    -> {
                    onIntentClick?.let { onClick ->
                        AdaptiveIntentPreferenceItem(
                            preferences = preferences,
                            intentKey = key,
                            onClick = onClick
                        )
                    }
                }

                else                    -> {
                    onIntentClick?.let { onClick ->
                        AdaptiveIntentPreferenceItem(
                            preferences = preferences,
                            intentKey = key,
                            onClick = onClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * LazyListScope extension for rendering a preference based on its PreferenceKey.
 */
fun LazyListScope.adaptivePreference(
    key: PreferenceKey,
    preferences: Preferences,
    config: Config,
    profileUtil: ProfileUtil? = null,
    onIntentClick: (() -> Unit)? = null,
    intentUrl: String? = null,
    intentActivityClass: Class<*>? = null,
    keyPrefix: String = ""
) {
    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${key.key}" else key.key

    item(key = itemKey, contentType = "AdaptivePreference_${key::class.simpleName}") {
        AdaptivePreferenceItem(
            key = key,
            preferences = preferences,
            config = config,
            profileUtil = profileUtil,
            onIntentClick = onIntentClick,
            intentUrl = intentUrl,
            intentActivityClass = intentActivityClass
        )
    }
}

/**
 * Renders a list of preferences from PreferenceKeys.
 * This is the main entry point for auto-generating preference screens.
 *
 * @param keys List of PreferenceKeys to render
 * @param preferences The Preferences instance
 * @param config The Config instance
 * @param profileUtil Required for UnitDoublePreferenceKey
 * @param intentHandlers Map of IntentPreferenceKey to handler info (optional - for dynamic values)
 */
@Composable
fun AdaptivePreferenceList(
    keys: List<PreferenceKey>,
    preferences: Preferences,
    config: Config,
    profileUtil: ProfileUtil? = null,
    intentHandlers: Map<IntentPreferenceKey, IntentHandler> = emptyMap()
) {
    keys.forEach { key ->
        if (key is IntentPreferenceKey) {
            // Priority: 1) intentHandlers map, 2) key properties
            val handler = intentHandlers[key]
            val resolvedUrl = handler?.url
                ?: key.urlResId?.let { stringResource(it) }
            val resolvedActivityClass = handler?.activityClass
                ?: key.activityClass
            val resolvedOnClick = handler?.onClick

            AdaptivePreferenceItem(
                key = key,
                preferences = preferences,
                config = config,
                profileUtil = profileUtil,
                onIntentClick = resolvedOnClick,
                intentUrl = resolvedUrl,
                intentActivityClass = resolvedActivityClass
            )
        } else {
            AdaptivePreferenceItem(
                key = key,
                preferences = preferences,
                config = config,
                profileUtil = profileUtil
            )
        }
    }
}

/**
 * Handler info for IntentPreferenceKey.
 * Provide one of: onClick, url, or activityClass based on preferenceType.
 */
data class IntentHandler(
    val onClick: (() -> Unit)? = null,
    val url: String? = null,
    val activityClass: Class<*>? = null
)
