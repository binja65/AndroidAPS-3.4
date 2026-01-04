package app.aaps.compose.preferences

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.protection.PasswordCheck
import app.aaps.core.interfaces.protection.ProtectionCheck.ProtectionType
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.OkDialog
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.core.ui.compose.preference.rememberPreferenceIntState
import app.aaps.core.ui.compose.preference.rememberPreferenceStringState

/**
 * Compose implementation of Protection preferences.
 * Note: Uses custom rendering due to conditional password/PIN fields.
 */
class ProtectionPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val passwordCheck: PasswordCheck
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.plugins.configuration.R.string.protection

    override val mainKeys: List<PreferenceKey> = listOf(
        IntKey.ProtectionTypeSettings,
        IntKey.ProtectionTypeApplication,
        IntKey.ProtectionTypeBolus,
        IntKey.ProtectionTimeout
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

        // Biometric fallback validation
        BiometricFallbackCheck(
            preferences = preferences,
            context = context,
            settingsProtectionState = settingsProtectionState,
            appProtectionState = appProtectionState,
            bolusProtectionState = bolusProtectionState
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

/**
 * Validates biometric protection settings and shows warning dialogs when:
 * 1. Biometric protection is activated without a master password
 * 2. Master password is erased while biometric protection is active
 */
@Composable
private fun BiometricFallbackCheck(
    preferences: Preferences,
    context: Context,
    settingsProtectionState: Int,
    appProtectionState: Int,
    bolusProtectionState: Int
) {
    val masterPasswordState by rememberPreferenceStringState(preferences, StringKey.ProtectionMasterPassword)
    val masterPassword = masterPasswordState

    // Dialog state
    var showBiometricWithoutPasswordDialog by remember { mutableStateOf(false) }
    var showPasswordErasedDialog by remember { mutableStateOf(false) }

    // Track previous values to detect changes
    var prevSettingsProtection by remember { mutableStateOf(settingsProtectionState) }
    var prevAppProtection by remember { mutableStateOf(appProtectionState) }
    var prevBolusProtection by remember { mutableStateOf(bolusProtectionState) }
    var prevMasterPassword by remember { mutableStateOf(masterPassword) }

    // Check for biometric protection activated without master password
    LaunchedEffect(settingsProtectionState, appProtectionState, bolusProtectionState, masterPassword) {
        val settingsChanged = settingsProtectionState != prevSettingsProtection
        val appChanged = appProtectionState != prevAppProtection
        val bolusChanged = bolusProtectionState != prevBolusProtection

        if ((settingsChanged && settingsProtectionState == ProtectionType.BIOMETRIC.ordinal) ||
            (appChanged && appProtectionState == ProtectionType.BIOMETRIC.ordinal) ||
            (bolusChanged && bolusProtectionState == ProtectionType.BIOMETRIC.ordinal)) {

            if (masterPassword.isEmpty()) {
                showBiometricWithoutPasswordDialog = true
            }
        }

        prevSettingsProtection = settingsProtectionState
        prevAppProtection = appProtectionState
        prevBolusProtection = bolusProtectionState
    }

    // Check for master password erased with biometric protection active
    LaunchedEffect(masterPassword, settingsProtectionState, appProtectionState, bolusProtectionState) {
        val masterPasswordChanged = masterPassword != prevMasterPassword
        val isBiometricActivated =
            settingsProtectionState == ProtectionType.BIOMETRIC.ordinal ||
            appProtectionState == ProtectionType.BIOMETRIC.ordinal ||
            bolusProtectionState == ProtectionType.BIOMETRIC.ordinal

        if (masterPasswordChanged && masterPassword.isEmpty() && isBiometricActivated) {
            showPasswordErasedDialog = true
        }

        prevMasterPassword = masterPassword
    }

    // Dialog: Biometric activated without master password
    if (showBiometricWithoutPasswordDialog) {
        OkDialog(
            title = stringResource(app.aaps.core.ui.R.string.unsecure_fallback_biometric),
            message = stringResource(
                app.aaps.plugins.configuration.R.string.master_password_missing,
                stringResource(app.aaps.plugins.configuration.R.string.protection)
            ),
            onDismiss = { showBiometricWithoutPasswordDialog = false }
        )
    }

    // Dialog: Master password erased with biometric active
    if (showPasswordErasedDialog) {
        OkDialog(
            title = stringResource(app.aaps.core.ui.R.string.unsecure_fallback_biometric),
            message = stringResource(app.aaps.core.ui.R.string.unsecure_fallback_descriotion_biometric),
            onDismiss = { showPasswordErasedDialog = false }
        )
    }
}
