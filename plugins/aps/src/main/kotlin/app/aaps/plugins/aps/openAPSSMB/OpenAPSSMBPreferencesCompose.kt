package app.aaps.plugins.aps.openAPSSMB

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.UnitDoubleKey
import app.aaps.core.keys.interfaces.PreferenceItem
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.rememberPreferenceBooleanState
import app.aaps.plugins.aps.keys.ApsIntentKey
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreenDef
import app.aaps.plugins.aps.R

/**
 * Compose implementation of OpenAPS SMB preferences.
 * Uses lightweight PreferenceSubScreenDef with auto-generated content.
 */
class OpenAPSSMBPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val profileUtil: ProfileUtil,
    private val activePlugin: ActivePlugin
) : NavigablePreferenceContent {

    private val visibilityContext = object : PreferenceVisibilityContext {
        override val preferences: Preferences = this@OpenAPSSMBPreferencesCompose.preferences
        override val isPatchPump: Boolean get() = activePlugin.activePump.pumpDescription.isPatchPump
        override val isBatteryReplaceable: Boolean get() = activePlugin.activePump.pumpDescription.isBatteryReplaceable
        override val isBatteryChangeLoggingEnabled: Boolean get() = false
        override val advancedFilteringSupported: Boolean get() = activePlugin.activeBgSource.advancedFilteringSupported()
    }

    override val titleResId: Int = R.string.openapssmb

    // Unified list with lightweight subscreen definition
    override val items: List<PreferenceItem> = listOf(
        // Main preferences (visibility handled dynamically in mainContent)
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
        IntKey.ApsCarbsRequestThreshold,

        // Advanced settings subscreen (auto-generated content)
        PreferenceSubScreenDef(
            key = "absorption_smb_advanced",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            keys = listOf(
                ApsIntentKey.LinkToDocs,
                BooleanKey.ApsAlwaysUseShortDeltas,
                DoubleKey.ApsMaxDailyMultiplier,
                DoubleKey.ApsMaxCurrentBasalMultiplier
            )
        )
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Shared state registry ensures reactivity - state reads inside derivedStateOf
        // are automatically tracked for dependency changes
        val smbEnabledState = rememberPreferenceBooleanState(preferences, BooleanKey.ApsUseSmb)
        val smbAlwaysEnabledState = rememberPreferenceBooleanState(preferences, BooleanKey.ApsUseSmbAlways)
        val uamEnabledState = rememberPreferenceBooleanState(preferences, BooleanKey.ApsUseUam)
        val dynIsfEnabledState = rememberPreferenceBooleanState(preferences, BooleanKey.ApsUseDynamicSensitivity)
        val dynIsfAdjustSensState = rememberPreferenceBooleanState(preferences, BooleanKey.ApsDynIsfAdjustSensitivity)
        val autoSensEnabledState = rememberPreferenceBooleanState(preferences, BooleanKey.ApsUseAutosens)

        // SMB-specific visibility logic - derivedStateOf auto-tracks state dependencies
        val filteredKeys by remember {
            derivedStateOf {
                val smbEnabled = smbEnabledState.value
                val smbAlwaysEnabled = smbAlwaysEnabledState.value
                val uamEnabled = uamEnabledState.value
                val advancedFiltering = activePlugin.activeBgSource.advancedFilteringSupported()
                val autoSensOrDynIsfSensEnabled = if (dynIsfEnabledState.value) {
                    dynIsfAdjustSensState.value
                } else {
                    autoSensEnabledState.value
                }

                mainKeys.filter { key ->
                    when (key) {
                        BooleanKey.ApsUseSmbAlways -> smbEnabled && advancedFiltering
                        BooleanKey.ApsUseSmbAfterCarbs -> smbEnabled && !smbAlwaysEnabled && advancedFiltering
                        BooleanKey.ApsResistanceLowersTarget -> autoSensOrDynIsfSensEnabled
                        BooleanKey.ApsSensitivityRaisesTarget -> autoSensOrDynIsfSensEnabled
                        IntKey.ApsUamMaxMinutesOfBasalToLimitSmb -> smbEnabled && uamEnabled
                        else -> true
                    }
                }
            }
        }

        AdaptivePreferenceList(
            keys = filteredKeys,
            preferences = preferences,
            config = config,
            profileUtil = profileUtil,
            visibilityContext = visibilityContext
        )
    }

    // Auto-generate content for PreferenceSubScreenDef (when customContent is null)
    @Composable
    override fun renderAutoGeneratedContent(def: PreferenceSubScreenDef) {
        AdaptivePreferenceList(
            keys = def.keys,
            preferences = preferences,
            config = config
        )
    }
}
