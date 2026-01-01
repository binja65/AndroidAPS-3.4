package app.aaps.plugins.aps.openAPSAutoISF

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.plugins.aps.keys.ApsIntentKey
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.aps.R

/**
 * Compose implementation of OpenAPS AutoISF preferences.
 * Uses AdaptivePreferenceList for all content rendering.
 */
class OpenAPSAutoISFPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.openaps_auto_isf

    override val mainKeys: List<PreferenceKey> = listOf(
        DoubleKey.ApsMaxBasal,
        DoubleKey.ApsSmbMaxIob,
        BooleanKey.ApsUseAutosens,
        BooleanKey.ApsSensitivityRaisesTarget,
        BooleanKey.ApsResistanceLowersTarget,
        BooleanKey.ApsAutoIsfHighTtRaisesSens,
        BooleanKey.ApsAutoIsfLowTtLowersSens,
        IntKey.ApsAutoIsfHalfBasalExerciseTarget,
        BooleanKey.ApsUseSmb,
        BooleanKey.ApsUseSmbWithHighTt,
        BooleanKey.ApsUseSmbAlways,
        BooleanKey.ApsUseSmbWithCob,
        BooleanKey.ApsUseSmbWithLowTt,
        BooleanKey.ApsUseSmbAfterCarbs,
        BooleanKey.ApsUseUam,
        IntKey.ApsMaxSmbFrequency,
        IntKey.ApsMaxMinutesOfBasalToLimitSmb,
        IntKey.ApsUamMaxMinutesOfBasalToLimitSmb,
        IntKey.ApsCarbsRequestThreshold
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    // Keys for advanced subscreen (including docs link)
    private val advancedKeys: List<PreferenceKey> = listOf(
        ApsIntentKey.LinkToDocs,
        BooleanKey.ApsAlwaysUseShortDeltas,
        DoubleKey.ApsMaxDailyMultiplier,
        DoubleKey.ApsMaxCurrentBasalMultiplier
    )

    // Keys for AutoISF settings subscreen
    private val autoIsfKeys: List<PreferenceKey> = listOf(
        BooleanKey.ApsUseAutoIsfWeights,
        DoubleKey.ApsAutoIsfMin,
        DoubleKey.ApsAutoIsfMax,
        DoubleKey.ApsAutoIsfBgAccelWeight,
        DoubleKey.ApsAutoIsfBgBrakeWeight,
        DoubleKey.ApsAutoIsfLowBgWeight,
        DoubleKey.ApsAutoIsfHighBgWeight,
        DoubleKey.ApsAutoIsfPpWeight,
        DoubleKey.ApsAutoIsfDuraWeight,
        IntKey.ApsAutoIsfIobThPercent
    )

    // Keys for SMB Delivery settings subscreen
    private val smbDeliveryKeys: List<PreferenceKey> = listOf(
        DoubleKey.ApsAutoIsfSmbDeliveryRatio,
        DoubleKey.ApsAutoIsfSmbDeliveryRatioMin,
        DoubleKey.ApsAutoIsfSmbDeliveryRatioMax,
        DoubleKey.ApsAutoIsfSmbDeliveryRatioBgRange,
        DoubleKey.ApsAutoIsfSmbMaxRangeExtension,
        BooleanKey.ApsAutoIsfSmbOnEvenTarget
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
        },

        // AutoISF settings subscreen
        PreferenceSubScreen(
            key = "auto_isf_settings",
            titleResId = R.string.autoISF_settings_title,
            keys = autoIsfKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = autoIsfKeys,
                preferences = preferences,
                config = config
            )
        },

        // SMB Delivery settings subscreen
        PreferenceSubScreen(
            key = "smb_delivery_settings",
            titleResId = R.string.smb_delivery_settings_title,
            keys = smbDeliveryKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = smbDeliveryKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
