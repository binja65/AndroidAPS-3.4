package app.aaps.compose.preferences

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

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
import app.aaps.core.ui.compose.preference.PreferenceSubScreenDef
import app.aaps.core.ui.compose.preference.rememberPreferenceIntState

/**
 * Compose implementation of Protection preferences.
 * Note: Uses custom rendering due to conditional password/PIN fields.
 */
fun ProtectionPreferencesCompose(
    preferences: Preferences,
    config: Config,
    passwordCheck: PasswordCheck
) = PreferenceSubScreenDef(
    key = "protection",
    titleResId = app.aaps.plugins.configuration.R.string.protection,
    customContent = { _ ->
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

        // Biometric fallback validation
        BiometricFallbackCheck(
            preferences = preferences,
            context = context,
            settingsProtectionState = settingsProtectionState,
            appProtectionState = appProtectionState,
            bolusProtectionState = bolusProtectionState
        )
    }
)
