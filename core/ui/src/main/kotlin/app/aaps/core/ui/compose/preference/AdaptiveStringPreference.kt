/*
 * Adaptive String Preference for Jetpack Compose
 */

package app.aaps.core.ui.compose.preference

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.StringPreferenceKey

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
