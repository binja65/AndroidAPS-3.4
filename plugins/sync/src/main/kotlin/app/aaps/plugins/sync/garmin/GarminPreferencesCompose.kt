package app.aaps.plugins.sync.garmin

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.plugins.sync.R
import app.aaps.plugins.sync.garmin.keys.GarminBooleanKey
import app.aaps.plugins.sync.garmin.keys.GarminIntKey
import app.aaps.plugins.sync.garmin.keys.GarminStringKey

/**
 * Compose implementation of Garmin preferences.
 */
class GarminPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Garmin settings category
        val garminSettingsKey = "${keyPrefix}_garmin_settings"
        item {
            val isExpanded = sectionState?.isExpanded(garminSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.garmin,
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(garminSettingsKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = GarminBooleanKey.LocalHttpServer,
                    titleResId = R.string.garmin_local_http_server
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = GarminIntKey.LocalHttpPort,
                    titleResId = R.string.garmin_local_http_server_port
                )

                AdaptiveStringPreferenceItem(
                    preferences = preferences,
                    config = config,
                    stringKey = GarminStringKey.RequestKey,
                    titleResId = R.string.garmin_request_key,
                    summaryResId = R.string.garmin_request_key_summary
                )
            }
        }
    }
}
