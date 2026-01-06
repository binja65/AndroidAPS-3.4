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
import app.aaps.core.keys.R
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.UnitDoublePreferenceKey
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

/**
 * Composable unit double preference for use inside card sections.
 * Handles glucose unit conversion (mg/dL <-> mmol/L).
 *
 * @param titleResId Optional title resource ID. If 0 or not provided, uses unitKey.titleResId
 * @param visibilityContext Optional context for evaluating runtime visibility/enabled conditions
 */
@Composable
fun AdaptiveUnitDoublePreferenceItem(
    preferences: Preferences,
    config: Config,
    profileUtil: ProfileUtil,
    unitKey: UnitDoublePreferenceKey,
    titleResId: Int = 0,
    visibilityContext: PreferenceVisibilityContext? = null
) {
    val effectiveTitleResId = if (titleResId != 0) titleResId else unitKey.titleResId

    // Skip if no title resource is available
    if (effectiveTitleResId == 0) return

    val visibility = calculatePreferenceVisibility(
        preferenceKey = unitKey,
        preferences = preferences,
        config = config,
        visibilityContext = visibilityContext
    )

    if (!visibility.visible || (preferences.simpleMode && unitKey.defaultedBySM)) return

    val state = rememberUnitDoublePreferenceState(preferences, profileUtil, unitKey)

    // Convert min/max values from mg/dL to current units using ProfileUtil
    val minDisplay = profileUtil.fromMgdlToUnits(unitKey.minMgdl.toDouble())
    val maxDisplay = profileUtil.fromMgdlToUnits(unitKey.maxMgdl.toDouble())

    // Detect if using mg/dL by checking if conversion preserved the value
    val isMgdl = abs(minDisplay - unitKey.minMgdl.toDouble()) < 0.01

    val precision = if (isMgdl) 0 else 1
    val minFormatted = BigDecimal(minDisplay).setScale(precision, RoundingMode.HALF_UP).toPlainString()
    val maxFormatted = BigDecimal(maxDisplay).setScale(precision, RoundingMode.HALF_UP).toPlainString()

    val textState = remember { mutableStateOf(state.displayValue) }

    // Choose the appropriate formatted unit string based on user's glucose unit preference
    val unitFormatResId = if (isMgdl) R.string.units_format_mgdl_range else R.string.units_format_mmol_range

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
        summary = { Text(stringResource(unitFormatResId, state.displayValue, minFormatted, maxFormatted)) }
    )
}
