package app.aaps.compose.preferences

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.protection.ProtectionCheck.ProtectionType
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.OkDialog
import app.aaps.core.ui.compose.preference.rememberPreferenceStringState

/**
 * Validates biometric protection settings and shows warning dialogs when:
 * 1. Biometric protection is activated without a master password
 * 2. Master password is erased while biometric protection is active
 */
@Composable
internal fun BiometricFallbackCheck(
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
