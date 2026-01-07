package app.aaps.core.ui.compose.preference

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.protection.PasswordCheck
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.R

/**
 * Master password preference that requires current password verification before allowing change.
 *
 * Logic:
 * - If master password is set: user must enter current password before setting new one
 * - If master password is not set: user can set it directly
 *
 * @param preferences The Preferences instance
 * @param config The Config instance
 * @param passwordCheck The PasswordCheck service for password operations
 * @param context Android context for dialogs
 */
@Composable
fun AdaptiveMasterPasswordPreferenceItem(
    preferences: Preferences,
    config: Config,
    passwordCheck: PasswordCheck,
    context: Context
) {
    val stringKey = StringKey.ProtectionMasterPassword

    val visibility = calculatePreferenceVisibility(
        preferenceKey = stringKey,
        preferences = preferences,
        config = config
    )

    if (!visibility.visible) return

    val passwordState by rememberPreferenceStringState(preferences, stringKey)
    val hasPassword = passwordState.isNotEmpty()

    val summary = if (hasPassword) {
        stringResource(R.string.password_set)
    } else {
        stringResource(R.string.password_not_set)
    }

    Preference(
        title = { Text(stringResource(app.aaps.core.keys.R.string.master_password)) },
        summary = { Text(summary) },
        enabled = visibility.enabled,
        onClick = if (visibility.enabled) {
            {
                if (hasPassword) {
                    // Password exists - query current password first
                    passwordCheck.queryPassword(
                        context = context,
                        labelId = R.string.current_master_password,
                        preference = stringKey,
                        ok = {
                            passwordCheck.setPassword(
                                context = context,
                                labelId = app.aaps.core.keys.R.string.master_password,
                                preference = stringKey
                            )
                        }
                    )
                } else {
                    // No password - set directly
                    passwordCheck.setPassword(
                        context = context,
                        labelId = app.aaps.core.keys.R.string.master_password,
                        preference = stringKey
                    )
                }
            }
        } else null
    )
}
