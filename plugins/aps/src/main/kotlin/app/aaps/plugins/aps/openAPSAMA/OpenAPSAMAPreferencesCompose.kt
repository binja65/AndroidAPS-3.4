package app.aaps.plugins.aps.openAPSAMA

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.plugins.aps.keys.ApsIntentKey
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.aps.R

/**
 * Compose implementation of OpenAPS AMA preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class OpenAPSAMAPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.openapsama

    override val mainKeys: List<PreferenceKey> = listOf(
        DoubleKey.ApsMaxBasal,
        DoubleKey.ApsAmaMaxIob,
        BooleanKey.ApsUseAutosens,
        BooleanKey.ApsAmaAutosensAdjustTargets,
        DoubleKey.ApsAmaMin5MinCarbsImpact
    )

    private val advancedKeys: List<PreferenceKey> = listOf(
        ApsIntentKey.LinkToDocs,
        BooleanKey.ApsAlwaysUseShortDeltas,
        DoubleKey.ApsMaxDailyMultiplier,
        DoubleKey.ApsMaxCurrentBasalMultiplier,
        DoubleKey.ApsAmaBolusSnoozeDivisor
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        PreferenceSubScreen(
            key = "absorption_ama_advanced",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            keys = advancedKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = advancedKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
