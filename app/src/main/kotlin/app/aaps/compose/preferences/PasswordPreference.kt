package app.aaps.compose.preferences

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.protection.PasswordCheck
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.rememberPreferenceStringState

/**
 * Password/PIN preference that opens a dialog to set the password.
 */
@Composable
internal fun PasswordPreference(
    titleResId: Int,
    stringKey: StringKey,
    preferences: Preferences,
    passwordCheck: PasswordCheck,
    context: Context,
    isPin: Boolean
) {
    val passwordState by rememberPreferenceStringState(preferences, stringKey)
    val hasValue = passwordState.isNotEmpty()
    val summary = when {
        hasValue -> "••••••••"
        isPin -> stringResource(app.aaps.core.ui.R.string.pin_not_set)
        else -> stringResource(app.aaps.core.ui.R.string.password_not_set)
    }

    Preference(
        title = { Text(stringResource(titleResId)) },
        summary = { Text(summary) },
        onClick = {
            passwordCheck.setPassword(
                context = context,
                labelId = titleResId,
                preference = stringKey,
                pinInput = isPin
            )
        }
    )
}
