package app.aaps.pump.diaconn

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceListForListKeys
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.navigable.PreferenceSubScreen
import app.aaps.pump.diaconn.keys.DiaconnBooleanKey
import app.aaps.pump.diaconn.keys.DiaconnIntKey

/**
 * Compose implementation of Diaconn G8 preferences.
 * Note: BT selector requires activity launch.
 */
class DiaconnG8PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val btSelectorActivityClass: Class<out Activity>? = null
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.diaconn_g8_pump

    override val mainKeys: List<PreferenceKey> = listOf(
        DiaconnIntKey.BolusSpeed,
        DiaconnBooleanKey.LogInsulinChange,
        DiaconnBooleanKey.LogCannulaChange,
        DiaconnBooleanKey.LogTubeChange,
        DiaconnBooleanKey.LogBatteryChange,
        DiaconnBooleanKey.SendLogsToCloud
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Bluetooth device selector - requires activity launch
        btSelectorActivityClass?.let { activityClass ->
            BtSelectorPreferenceItem(
                titleResId = R.string.selectedpump,
                activityClass = activityClass
            )
        }

        // All other preferences are key-based (BolusSpeed has entries on key)
        AdaptivePreferenceListForListKeys(
            keys = listOf(
                DiaconnIntKey.BolusSpeed,
                DiaconnBooleanKey.LogInsulinChange,
                DiaconnBooleanKey.LogCannulaChange,
                DiaconnBooleanKey.LogTubeChange,
                DiaconnBooleanKey.LogBatteryChange,
                DiaconnBooleanKey.SendLogsToCloud
            ),
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}

/**
 * Composable preference for launching BLE scan activity.
 */
@Composable
private fun BtSelectorPreferenceItem(
    titleResId: Int,
    activityClass: Class<out Activity>
) {
    val context = LocalContext.current
    Preference(
        title = { Text(stringResource(titleResId)) },
        onClick = {
            context.startActivity(Intent(context, activityClass))
        }
    )
}
