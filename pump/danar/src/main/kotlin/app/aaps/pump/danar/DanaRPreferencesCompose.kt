package app.aaps.pump.danar

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.dana.R
import app.aaps.pump.dana.keys.DanaBooleanKey
import app.aaps.pump.dana.keys.DanaIntKey
import app.aaps.pump.dana.keys.DanaStringKey

/**
 * Compose implementation of DanaR preferences.
 */
class DanaRPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.danarpump

    override val summaryItems: List<Int> = listOf(
        R.string.danar_bt_name_title,
        R.string.danar_password_title,
        R.string.bolusspeed
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Bluetooth device selector
        BluetoothDevicePreferenceItem(
            preferences = preferences,
            config = config
        )

        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = DanaIntKey.Password,
            titleResId = R.string.danar_password_title
        )

        AdaptiveListIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = DanaIntKey.BolusSpeed,
            titleResId = R.string.bolusspeed,
            entries = listOf("12 s/U", "30 s/U", "60 s/U"),
            entryValues = listOf(0, 1, 2)
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = DanaBooleanKey.UseExtended,
            titleResId = R.string.danar_useextended_title
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}

/**
 * Composable preference for selecting a bonded Bluetooth device.
 * Shows a list of classic Bluetooth bonded devices.
 */
@Composable
private fun BluetoothDevicePreferenceItem(
    preferences: Preferences,
    config: Config
) {
    val context = LocalContext.current

    // Get bonded devices if permission is granted
    val bondedDevices = remember {
        getBondedBluetoothDevices(context)
    }

    if (bondedDevices.isNotEmpty()) {
        // Create entries map from bonded devices (name -> name)
        val entries = bondedDevices.associateWith { it }

        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = DanaStringKey.RName,
            titleResId = R.string.danar_bt_name_title,
            entries = entries
        )
    } else {
        // Show placeholder when no devices or no permission
        Preference(
            title = { Text(stringResource(R.string.danar_bt_name_title)) },
            summary = { Text(stringResource(app.aaps.core.ui.R.string.need_connect_permission)) },
            enabled = false
        )
    }
}

/**
 * Gets list of bonded classic Bluetooth device names.
 * Returns empty list if permission is not granted.
 */
private fun getBondedBluetoothDevices(context: Context): List<String> {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        return emptyList()
    }

    return try {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        bluetoothManager?.adapter?.bondedDevices
            ?.mapNotNull { it.name }
            ?.sorted()
            ?: emptyList()
    } catch (e: SecurityException) {
        emptyList()
    }
}
