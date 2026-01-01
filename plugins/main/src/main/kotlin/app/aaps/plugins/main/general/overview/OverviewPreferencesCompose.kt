package app.aaps.plugins.main.general.overview

import android.content.Context
import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.nsclient.NSSettingsStatus
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.UnitDoubleKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.IntentHandler
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.main.R
import app.aaps.plugins.main.general.overview.keys.OverviewIntentKey

/**
 * Compose implementation of Overview preferences.
 * Uses key-based rendering with AdaptivePreferenceList.
 */
class OverviewPreferencesCompose(
    private val rh: ResourceHelper,
    private val activePlugin: ActivePlugin,
    private val nsSettingStatus: NSSettingsStatus,
    private val preferences: Preferences,
    private val config: Config,
    private val profileUtil: ProfileUtil,
    private val context: Context,
    private val quickWizardListActivity: Class<*>? = null
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.overview

    override val mainKeys: List<PreferenceKey> = listOf(
        BooleanKey.OverviewKeepScreenOn,
        OverviewIntentKey.QuickWizardSettings,
        BooleanKey.OverviewShortTabTitles,
        BooleanKey.OverviewShowNotesInDialogs,
        IntKey.OverviewBolusPercentage,
        IntKey.OverviewResetBolusPercentageTime,
        BooleanKey.OverviewUseBolusAdvisor,
        BooleanKey.OverviewUseBolusReminder
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config,
            profileUtil = profileUtil,
            intentHandlers = quickWizardListActivity?.let {
                mapOf(OverviewIntentKey.QuickWizardSettings to IntentHandler(activityClass = it))
            } ?: emptyMap()
        )
    }

    // Keys for buttons settings subscreen
    private val buttonsKeys: List<PreferenceKey> = listOf(
        BooleanKey.OverviewShowTreatmentButton,
        BooleanKey.OverviewShowWizardButton,
        BooleanKey.OverviewShowInsulinButton,
        DoubleKey.OverviewInsulinButtonIncrement1,
        DoubleKey.OverviewInsulinButtonIncrement2,
        DoubleKey.OverviewInsulinButtonIncrement3,
        BooleanKey.OverviewShowCarbsButton,
        IntKey.OverviewCarbsButtonIncrement1,
        IntKey.OverviewCarbsButtonIncrement2,
        IntKey.OverviewCarbsButtonIncrement3,
        BooleanKey.OverviewShowCgmButton,
        BooleanKey.OverviewShowCalibrationButton
    )

    // Keys for default temp targets subscreen
    private val tempTargetsKeys: List<PreferenceKey> = listOf(
        IntKey.OverviewEatingSoonDuration,
        UnitDoubleKey.OverviewEatingSoonTarget,
        IntKey.OverviewActivityDuration,
        UnitDoubleKey.OverviewActivityTarget,
        IntKey.OverviewHypoDuration,
        UnitDoubleKey.OverviewHypoTarget
    )

    // Keys for prime/fill settings subscreen
    private val fillKeys: List<PreferenceKey> = listOf(
        DoubleKey.ActionsFillButton1,
        DoubleKey.ActionsFillButton2,
        DoubleKey.ActionsFillButton3
    )

    // Keys for range settings subscreen
    private val rangeKeys: List<PreferenceKey> = listOf(
        UnitDoubleKey.OverviewLowMark,
        UnitDoubleKey.OverviewHighMark
    )

    // Keys for status lights - common keys always shown
    private val statusLightsCommonKeys: List<PreferenceKey> = listOf(
        BooleanKey.OverviewShowStatusLights,
        IntKey.OverviewCageWarning,
        IntKey.OverviewCageCritical
    )

    // Keys for status lights - insulin age keys (only for non-patch pumps)
    private val statusLightsInsulinAgeKeys: List<PreferenceKey> = listOf(
        IntKey.OverviewIageWarning,
        IntKey.OverviewIageCritical
    )

    // Keys for status lights - remaining keys
    private val statusLightsRemainingKeys: List<PreferenceKey> = listOf(
        IntKey.OverviewSageWarning,
        IntKey.OverviewSageCritical,
        IntKey.OverviewSbatWarning,
        IntKey.OverviewSbatCritical,
        IntKey.OverviewResWarning,
        IntKey.OverviewResCritical,
        IntKey.OverviewBattWarning,
        IntKey.OverviewBattCritical,
        IntKey.OverviewBageWarning,
        IntKey.OverviewBageCritical
    )

    // Keys for advanced settings subscreen
    private val advancedKeys: List<PreferenceKey> = listOf(
        BooleanKey.OverviewUseSuperBolus
    )

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Buttons Settings subscreen
        PreferenceSubScreen(
            key = "overview_buttons_settings",
            titleResId = R.string.overview_buttons_selection,
            keys = buttonsKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = buttonsKeys,
                preferences = preferences,
                config = config
            )
        },

        // Default Temp Targets subscreen
        PreferenceSubScreen(
            key = "default_temp_targets_settings",
            titleResId = R.string.default_temptargets,
            keys = tempTargetsKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = tempTargetsKeys,
                preferences = preferences,
                config = config,
                profileUtil = profileUtil
            )
        },

        // Prime/Fill Settings subscreen
        PreferenceSubScreen(
            key = "prime_fill_settings",
            titleResId = R.string.fill_bolus_title,
            keys = fillKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = fillKeys,
                preferences = preferences,
                config = config
            )
        },

        // Range Settings subscreen
        PreferenceSubScreen(
            key = "range_settings",
            titleResId = R.string.prefs_range_title,
            keys = rangeKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = rangeKeys,
                preferences = preferences,
                config = config,
                profileUtil = profileUtil
            )
        },

        // Status Lights subscreen - kept manual due to conditional isPatchPump logic and custom button
        PreferenceSubScreen(
            key = "statuslights_overview_advanced",
            titleResId = app.aaps.core.ui.R.string.statuslights,
            keys = statusLightsCommonKeys + statusLightsRemainingKeys
        ) { _ ->
            val isPatchPump = activePlugin.activePump.pumpDescription.isPatchPump

            // Common keys
            AdaptivePreferenceList(
                keys = statusLightsCommonKeys,
                preferences = preferences,
                config = config
            )

            // Insulin age keys - only for non-patch pumps
            if (!isPatchPump) {
                AdaptivePreferenceList(
                    keys = statusLightsInsulinAgeKeys,
                    preferences = preferences,
                    config = config
                )
            }

            // Remaining keys
            AdaptivePreferenceList(
                keys = statusLightsRemainingKeys,
                preferences = preferences,
                config = config
            )

            // Copy settings from NS button
            Preference(
                title = { androidx.compose.material3.Text(rh.gs(R.string.statuslights_copy_ns)) },
                onClick = {
                    nsSettingStatus.copyStatusLightsNsSettings(context)
                }
            )
        },

        // Advanced Settings subscreen
        PreferenceSubScreen(
            key = "overview_advanced_settings",
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
