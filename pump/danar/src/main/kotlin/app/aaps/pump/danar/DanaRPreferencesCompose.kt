package app.aaps.pump.danar

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

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
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withEntries
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
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

    override val mainKeys: List<PreferenceKey> = listOf(
        DanaStringKey.RName,
        DanaIntKey.Password,
        DanaIntKey.BolusSpeed,
        DanaBooleanKey.UseExtended
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        val context = LocalContext.current
        val bondedDevices = remember { getBondedBluetoothDevices(context) }

        if (bondedDevices.isNotEmpty()) {
            AdaptivePreferenceList(
                keys = listOf(
                    DanaStringKey.RName.withEntries(bondedDevices.associateWith { it }),
                    DanaIntKey.Password,
                    DanaIntKey.BolusSpeed,
                    DanaBooleanKey.UseExtended
                ),
                preferences = preferences,
                config = config
            )
        } else {
            // Show placeholder when no devices or no permission
            Preference(
                title = { Text(stringResource(R.string.danar_bt_name_title)) },
                summary = { Text(stringResource(app.aaps.core.ui.R.string.need_connect_permission)) },
                enabled = false
            )
            AdaptivePreferenceList(
                keys = listOf(DanaIntKey.Password, DanaIntKey.BolusSpeed, DanaBooleanKey.UseExtended),
                preferences = preferences,
                config = config
            )
        }
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
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
