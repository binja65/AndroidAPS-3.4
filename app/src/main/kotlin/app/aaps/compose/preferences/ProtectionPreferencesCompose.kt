package app.aaps.compose.preferences

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.ui.platform.LocalContext
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.protection.PasswordCheck
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.ProtectionType
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptivePasswordPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptivePreferenceItem
import app.aaps.core.ui.compose.preference.PreferenceSubScreenDef

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

        // Master Password
        MasterPasswordPreference(
            preferences = preferences,
            passwordCheck = passwordCheck,
            context = context
        )

        // Settings Protection (uses entries from IntKey)
        AdaptivePreferenceItem(
            key = IntKey.ProtectionTypeSettings,
            preferences = preferences,
            config = config
        )

        // Settings Password (visible when CUSTOM_PASSWORD)
        AdaptivePasswordPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.ProtectionSettingsPassword,
            passwordCheck = passwordCheck,
            context = context,
            visibilityKey = IntKey.ProtectionTypeSettings,
            visibilityValue = ProtectionType.CUSTOM_PASSWORD.ordinal
        )

        // Settings PIN (visible when CUSTOM_PIN)
        AdaptivePasswordPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.ProtectionSettingsPin,
            passwordCheck = passwordCheck,
            context = context,
            visibilityKey = IntKey.ProtectionTypeSettings,
            visibilityValue = ProtectionType.CUSTOM_PIN.ordinal
        )

        // Application Protection (uses entries from IntKey)
        AdaptivePreferenceItem(
            key = IntKey.ProtectionTypeApplication,
            preferences = preferences,
            config = config
        )

        // Application Password (visible when CUSTOM_PASSWORD)
        AdaptivePasswordPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.ProtectionApplicationPassword,
            passwordCheck = passwordCheck,
            context = context,
            visibilityKey = IntKey.ProtectionTypeApplication,
            visibilityValue = ProtectionType.CUSTOM_PASSWORD.ordinal
        )

        // Application PIN (visible when CUSTOM_PIN)
        AdaptivePasswordPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.ProtectionApplicationPin,
            passwordCheck = passwordCheck,
            context = context,
            visibilityKey = IntKey.ProtectionTypeApplication,
            visibilityValue = ProtectionType.CUSTOM_PIN.ordinal
        )

        // Bolus Protection (uses entries from IntKey)
        AdaptivePreferenceItem(
            key = IntKey.ProtectionTypeBolus,
            preferences = preferences,
            config = config
        )

        // Bolus Password (visible when CUSTOM_PASSWORD)
        AdaptivePasswordPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.ProtectionBolusPassword,
            passwordCheck = passwordCheck,
            context = context,
            visibilityKey = IntKey.ProtectionTypeBolus,
            visibilityValue = ProtectionType.CUSTOM_PASSWORD.ordinal
        )

        // Bolus PIN (visible when CUSTOM_PIN)
        AdaptivePasswordPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.ProtectionBolusPin,
            passwordCheck = passwordCheck,
            context = context,
            visibilityKey = IntKey.ProtectionTypeBolus,
            visibilityValue = ProtectionType.CUSTOM_PIN.ordinal
        )

        // Protection Timeout
        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.ProtectionTimeout
        )
    }
)
