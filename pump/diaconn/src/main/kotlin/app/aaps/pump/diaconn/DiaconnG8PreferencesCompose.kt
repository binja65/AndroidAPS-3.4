package app.aaps.pump.diaconn

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveListIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.diaconn.keys.DiaconnBooleanKey
import app.aaps.pump.diaconn.keys.DiaconnIntKey

/**
 * Compose implementation of Diaconn G8 preferences.
 * Note: BT selector and bolus speed require special handling.
 */
class DiaconnG8PreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val btSelectorActivityClass: Class<out Activity>? = null
) : NavigablePreferenceContent {

    companion object {

        private val speedEntries = listOf("1 U/min", "2 U/min", "3 U/min", "4 U/min", "5 U/min", "6 U/min", "7 U/min", "8 U/min")
        private val speedValues = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    }

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
        // Bluetooth device selector
        btSelectorActivityClass?.let { activityClass ->
            BtSelectorPreferenceItem(
                titleResId = R.string.selectedpump,
                activityClass = activityClass
            )
        }

        // Bolus speed - requires custom entries
        AdaptiveListIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = DiaconnIntKey.BolusSpeed,
            titleResId = R.string.bolusspeed,
            entries = speedEntries,
            entryValues = speedValues
        )

        // Boolean preferences - can use key-based
        AdaptivePreferenceList(
            keys = listOf(
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
