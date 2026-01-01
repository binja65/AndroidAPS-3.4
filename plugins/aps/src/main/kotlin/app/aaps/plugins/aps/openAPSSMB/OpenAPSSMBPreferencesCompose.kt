package app.aaps.plugins.aps.openAPSSMB

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.UnitDoubleKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.plugins.aps.keys.ApsIntentKey
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.aps.R

/**
 * Compose implementation of OpenAPS SMB preferences.
 * Uses AdaptivePreferenceList for all content rendering.
 */
class OpenAPSSMBPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val profileUtil: ProfileUtil
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.openapssmb

    override val mainKeys: List<PreferenceKey> = listOf(
        DoubleKey.ApsMaxBasal,
        DoubleKey.ApsSmbMaxIob,
        BooleanKey.ApsUseDynamicSensitivity,
        BooleanKey.ApsUseAutosens,
        IntKey.ApsDynIsfAdjustmentFactor,
        UnitDoubleKey.ApsLgsThreshold,
        BooleanKey.ApsDynIsfAdjustSensitivity,
        BooleanKey.ApsSensitivityRaisesTarget,
        BooleanKey.ApsResistanceLowersTarget,
        BooleanKey.ApsUseSmb,
        BooleanKey.ApsUseSmbWithHighTt,
        BooleanKey.ApsUseSmbAlways,
        BooleanKey.ApsUseSmbWithCob,
        BooleanKey.ApsUseSmbWithLowTt,
        BooleanKey.ApsUseSmbAfterCarbs,
        IntKey.ApsMaxSmbFrequency,
        IntKey.ApsMaxMinutesOfBasalToLimitSmb,
        IntKey.ApsUamMaxMinutesOfBasalToLimitSmb,
        BooleanKey.ApsUseUam,
        IntKey.ApsCarbsRequestThreshold
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config,
            profileUtil = profileUtil
        )
    }

    // Keys for advanced subscreen (including docs link)
    private val advancedKeys: List<PreferenceKey> = listOf(
        ApsIntentKey.LinkToDocs,
        BooleanKey.ApsAlwaysUseShortDeltas,
        DoubleKey.ApsMaxDailyMultiplier,
        DoubleKey.ApsMaxCurrentBasalMultiplier
    )

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Advanced settings subscreen
        PreferenceSubScreen(
            key = "absorption_smb_advanced",
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
