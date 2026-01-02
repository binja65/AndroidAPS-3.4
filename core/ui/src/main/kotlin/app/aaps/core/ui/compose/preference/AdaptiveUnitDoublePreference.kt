/*
 * Adaptive Unit Double Preference for Jetpack Compose
 */

package app.aaps.core.ui.compose.preference

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.UnitDoublePreferenceKey
import java.math.BigDecimal
import java.math.RoundingMode

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
