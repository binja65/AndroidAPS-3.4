package app.aaps.plugins.automation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen

/**
 * Compose implementation of Automation preferences.
 */
class AutomationPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.core.ui.R.string.automation

    override val summaryItems: List<Int> = listOf(
        R.string.locationservice
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Location service preference
        val entries = mapOf(
            "PASSIVE" to stringResource(R.string.use_passive_location),
            "NETWORK" to stringResource(R.string.use_network_location),
            "GPS" to stringResource(R.string.use_gps_location)
        )
        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.AutomationLocation,
            titleResId = R.string.locationservice,
            entries = entries
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
