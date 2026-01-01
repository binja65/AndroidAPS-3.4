package app.aaps.plugins.automation

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.sharedPreferences.SP
import app.aaps.core.keys.StringKey
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.core.ui.compose.preference.StringListPreferenceItem

/**
 * Compose implementation of Automation preferences.
 */
class AutomationPreferencesCompose(
    private val sp: SP
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.core.ui.R.string.automation

    override val summaryItems: List<Int> = listOf(
        R.string.locationservice
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Location service preference
        StringListPreferenceItem(
            sp = sp,
            key = StringKey.AutomationLocation.key,
            defaultValue = StringKey.AutomationLocation.defaultValue,
            entries = mapOf(
                "PASSIVE" to R.string.use_passive_location,
                "NETWORK" to R.string.use_network_location,
                "GPS" to R.string.use_gps_location
            ),
            titleResId = R.string.locationservice
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
