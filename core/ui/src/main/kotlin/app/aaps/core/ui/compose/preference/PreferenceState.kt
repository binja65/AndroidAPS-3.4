/*
 * Preference State Support for Jetpack Compose
 * Provides state classes and visibility calculation for preferences
 */

package app.aaps.core.ui.compose.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.interfaces.BooleanPreferenceKey
import app.aaps.core.keys.interfaces.DoublePreferenceKey
import app.aaps.core.keys.interfaces.IntPreferenceKey
import app.aaps.core.keys.interfaces.IntentPreferenceKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.StringPreferenceKey
import app.aaps.core.keys.interfaces.UnitDoublePreferenceKey
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Data class holding visibility and enabled state for a preference
 */
data class PreferenceVisibilityState(
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
    engineeringModeOnly: Boolean = false,
    visibilityContext: PreferenceVisibilityContext? = null
): PreferenceVisibilityState {
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

    // Check runtime visibility condition
    visibilityContext?.let { context ->
        if (!preferenceKey.visibility.isVisible(context)) {
            visible = false
        }
    }

    return PreferenceVisibilityState(visible, enabled)
}

/**
 * Calculates visibility and enabled state for an intent preference based on mode settings and dependencies.
 */
fun calculateIntentPreferenceVisibility(
    intentKey: IntentPreferenceKey,
    preferences: Preferences,
    visibilityContext: PreferenceVisibilityContext? = null
): PreferenceVisibilityState {
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

    // Check runtime visibility condition
    visibilityContext?.let { context ->
        if (!intentKey.visibility.isVisible(context)) {
            visible = false
        }
    }

    return PreferenceVisibilityState(visible, enabled)
}

// =================================
// Remember State Functions
// =================================

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

// =================================
// State Classes
// =================================

@Stable
internal class PreferenceBooleanState(
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
internal class PreferenceStringState(
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
internal class PreferenceIntState(
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
internal class PreferenceDoubleState(
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
// Unit Double State
// =================================

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
