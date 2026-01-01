package app.aaps.compose.preferences

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.protection.PasswordCheck
import app.aaps.core.interfaces.protection.ProtectionCheck.ProtectionType
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.core.ui.compose.preference.rememberPreferenceIntState

/**
 * Compose implementation of Protection preferences.
 */
class ProtectionPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val passwordCheck: PasswordCheck
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.plugins.configuration.R.string.protection

    override val summaryItems: List<Int> = listOf(
        app.aaps.core.ui.R.string.master_password,
        app.aaps.core.ui.R.string.settings_protection,
        app.aaps.core.ui.R.string.application_protection,
        app.aaps.core.ui.R.string.bolus_protection
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        val context = LocalContext.current

        // Protection type entries
        val protectionTypeEntries = listOf(
            stringResource(app.aaps.core.ui.R.string.noprotection),
            stringResource(app.aaps.core.ui.R.string.biometric),
            stringResource(app.aaps.core.ui.R.string.master_password),
            stringResource(app.aaps.core.ui.R.string.custom_password),
            stringResource(app.aaps.core.ui.R.string.custom_pin)
        )
        val protectionTypeValues = listOf(0, 1, 2, 3, 4)

        // Master Password
        MasterPasswordPreference(
            preferences = preferences,
            passwordCheck = passwordCheck,
            context = context
        )

        // Settings Protection
        val settingsProtectionState by rememberPreferenceIntState(preferences, IntKey.ProtectionTypeSettings)

        AdaptiveListIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.ProtectionTypeSettings,
            titleResId = app.aaps.core.ui.R.string.settings_protection,
            entries = protectionTypeEntries,
            entryValues = protectionTypeValues
        )

        // Settings Password (visible when CUSTOM_PASSWORD)
        if (settingsProtectionState == ProtectionType.CUSTOM_PASSWORD.ordinal) {
            PasswordPreference(
                titleResId = app.aaps.core.ui.R.string.settings_password,
                stringKey = StringKey.ProtectionSettingsPassword,
                preferences = preferences,
                passwordCheck = passwordCheck,
                context = context,
                isPin = false
            )
        }

        // Settings PIN (visible when CUSTOM_PIN)
        if (settingsProtectionState == ProtectionType.CUSTOM_PIN.ordinal) {
            PasswordPreference(
                titleResId = app.aaps.core.ui.R.string.settings_pin,
                stringKey = StringKey.ProtectionSettingsPin,
                preferences = preferences,
                passwordCheck = passwordCheck,
                context = context,
                isPin = true
            )
        }

        // Application Protection
        val appProtectionState by rememberPreferenceIntState(preferences, IntKey.ProtectionTypeApplication)

        AdaptiveListIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.ProtectionTypeApplication,
            titleResId = app.aaps.core.ui.R.string.application_protection,
            entries = protectionTypeEntries,
            entryValues = protectionTypeValues
        )

        // Application Password (visible when CUSTOM_PASSWORD)
        if (appProtectionState == ProtectionType.CUSTOM_PASSWORD.ordinal) {
            PasswordPreference(
                titleResId = app.aaps.core.ui.R.string.application_password,
                stringKey = StringKey.ProtectionApplicationPassword,
                preferences = preferences,
                passwordCheck = passwordCheck,
                context = context,
                isPin = false
            )
        }

        // Application PIN (visible when CUSTOM_PIN)
        if (appProtectionState == ProtectionType.CUSTOM_PIN.ordinal) {
            PasswordPreference(
                titleResId = app.aaps.core.ui.R.string.application_pin,
                stringKey = StringKey.ProtectionApplicationPin,
                preferences = preferences,
                passwordCheck = passwordCheck,
                context = context,
                isPin = true
            )
        }

        // Bolus Protection
        val bolusProtectionState by rememberPreferenceIntState(preferences, IntKey.ProtectionTypeBolus)

        AdaptiveListIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.ProtectionTypeBolus,
            titleResId = app.aaps.core.ui.R.string.bolus_protection,
            entries = protectionTypeEntries,
            entryValues = protectionTypeValues
        )

        // Bolus Password (visible when CUSTOM_PASSWORD)
        if (bolusProtectionState == ProtectionType.CUSTOM_PASSWORD.ordinal) {
            PasswordPreference(
                titleResId = app.aaps.core.ui.R.string.bolus_password,
                stringKey = StringKey.ProtectionBolusPassword,
                preferences = preferences,
                passwordCheck = passwordCheck,
                context = context,
                isPin = false
            )
        }

        // Bolus PIN (visible when CUSTOM_PIN)
        if (bolusProtectionState == ProtectionType.CUSTOM_PIN.ordinal) {
            PasswordPreference(
                titleResId = app.aaps.core.ui.R.string.bolus_pin,
                stringKey = StringKey.ProtectionBolusPin,
                preferences = preferences,
                passwordCheck = passwordCheck,
                context = context,
                isPin = true
            )
        }

        // Protection Timeout
        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.ProtectionTimeout,
            titleResId = app.aaps.core.ui.R.string.protection_timeout_title
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}

/**
 * Master password preference that requires current password before allowing change.
 */
@Composable
private fun MasterPasswordPreference(
    preferences: Preferences,
    passwordCheck: PasswordCheck,
    context: Context
) {
    val hasPassword = preferences.get(StringKey.ProtectionMasterPassword).isNotEmpty()
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

/**
 * Password/PIN preference that opens a dialog to set the password.
 */
@Composable
private fun PasswordPreference(
    titleResId: Int,
    stringKey: StringKey,
    preferences: Preferences,
    passwordCheck: PasswordCheck,
    context: Context,
    isPin: Boolean
) {
    val hasValue = preferences.get(stringKey).isNotEmpty()
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
