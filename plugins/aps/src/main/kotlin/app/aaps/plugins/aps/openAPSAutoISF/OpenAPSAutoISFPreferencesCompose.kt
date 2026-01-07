package app.aaps.plugins.aps.openAPSAutoISF

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceListForListKeys
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.rememberPreferenceBooleanState
import app.aaps.plugins.aps.keys.ApsIntentKey
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.navigable.PreferenceSubScreen
import app.aaps.plugins.aps.R

/**
 * Compose implementation of OpenAPS AutoISF preferences.
 * Uses AdaptivePreferenceList for all content rendering.
 */
class OpenAPSAutoISFPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val activePlugin: ActivePlugin
) : NavigablePreferenceContent {

    private val visibilityContext = object : PreferenceVisibilityContext {
        override val preferences: Preferences = this@OpenAPSAutoISFPreferencesCompose.preferences
        override val isPatchPump: Boolean get() = activePlugin.activePump.pumpDescription.isPatchPump
        override val isBatteryReplaceable: Boolean get() = activePlugin.activePump.pumpDescription.isBatteryReplaceable
        override val isBatteryChangeLoggingEnabled: Boolean get() = false
        override val advancedFilteringSupported: Boolean get() = activePlugin.activeBgSource.advancedFilteringSupported()
    }

    override val titleResId: Int = R.string.openaps_auto_isf

    private val allMainKeys: List<PreferenceKey> = listOf(
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

    override val mainKeys: List<PreferenceKey> get() = allMainKeys

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Shared state registry ensures reactivity - state reads inside derivedStateOf
        // are automatically tracked for dependency changes
        val smbAlwaysEnabledState = rememberPreferenceBooleanState(preferences, BooleanKey.ApsUseSmbAlways)

        // AutoISF-specific visibility logic - derivedStateOf auto-tracks state dependencies
        val filteredKeys by remember {
            derivedStateOf {
                val smbAlwaysEnabled = smbAlwaysEnabledState.value
                val advancedFiltering = activePlugin.activeBgSource.advancedFilteringSupported()

                allMainKeys.filter { key ->
                    when (key) {
                        // AutoISF: visible when !smbAlways || !advancedFiltering
                        BooleanKey.ApsUseSmbAfterCarbs -> !smbAlwaysEnabled || !advancedFiltering
                        else -> true
                    }
                }
            }
        }

        AdaptivePreferenceListForListKeys(
            keys = filteredKeys,
            preferences = preferences,
            config = config,
            visibilityContext = visibilityContext
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
            AdaptivePreferenceListForListKeys(
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
            AdaptivePreferenceListForListKeys(
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
            AdaptivePreferenceListForListKeys(
                keys = smbDeliveryKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
