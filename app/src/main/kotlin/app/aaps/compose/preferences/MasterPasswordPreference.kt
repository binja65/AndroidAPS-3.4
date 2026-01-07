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
 * Master password preference that requires current password before allowing change.
 */
@Composable
internal fun MasterPasswordPreference(
    preferences: Preferences,
    passwordCheck: PasswordCheck,
    context: Context
) {
    val masterPasswordState by rememberPreferenceStringState(preferences, StringKey.ProtectionMasterPassword)
    val hasPassword = masterPasswordState.isNotEmpty()
    val summary = if (hasPassword) {
        stringResource(app.aaps.core.ui.R.string.password_set)
    } else {
        stringResource(app.aaps.core.ui.R.string.password_not_set)
    }

    Preference(
        title = { Text(stringResource(app.aaps.core.ui.R.string.master_password)) },
        summary = { Text(summary) },
        onClick = {
            passwordCheck.queryPassword(
                context = context,
                labelId = app.aaps.plugins.configuration.R.string.current_master_password,
                preference = StringKey.ProtectionMasterPassword,
                ok = {
                    passwordCheck.setPassword(
                        context = context,
                        labelId = app.aaps.core.ui.R.string.master_password,
                        preference = StringKey.ProtectionMasterPassword
                    )
                }
            )
        }
    )
}
