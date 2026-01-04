package app.aaps.plugins.main.general.overview

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.nsclient.NSSettingsStatus
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.UnitDoubleKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withActivity
import app.aaps.core.keys.interfaces.withClick
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.main.R
import app.aaps.plugins.main.general.overview.keys.OverviewIntKey
import app.aaps.plugins.main.general.overview.keys.OverviewIntentKey

/**
 * Compose implementation of Overview preferences.
 * Uses key-based rendering with AdaptivePreferenceList.
 */
class OverviewPreferencesCompose(
    private val nsSettingStatus: NSSettingsStatus,
    private val preferences: Preferences,
    private val config: Config,
    private val profileUtil: ProfileUtil,
    private val visibilityContext: PreferenceVisibilityContext,
    private val quickWizardListActivity: Class<*>? = null
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.overview

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = listOf(
                BooleanKey.OverviewKeepScreenOn,
                quickWizardListActivity?.let { OverviewIntentKey.QuickWizardSettings.withActivity(it) } ?: OverviewIntentKey.QuickWizardSettings,
                BooleanKey.OverviewShowNotesInDialogs,
                IntKey.OverviewBolusPercentage,
                IntKey.OverviewResetBolusPercentageTime,
                BooleanKey.OverviewUseBolusAdvisor,
                BooleanKey.OverviewUseBolusReminder
            ),
            preferences = preferences,
            config = config,
            profileUtil = profileUtil
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

    // Keys for status lights - visibility is handled by PreferenceVisibility on each key
    private val statusLightsKeys: List<PreferenceKey> = listOf(
        BooleanKey.OverviewShowStatusLights,
        IntKey.OverviewCageWarning,
        IntKey.OverviewCageCritical,
        OverviewIntKey.IageWarning,
        OverviewIntKey.IageCritical,
        IntKey.OverviewSageWarning,
        IntKey.OverviewSageCritical,
        IntKey.OverviewSbatWarning,
        IntKey.OverviewSbatCritical,
        IntKey.OverviewResWarning,
        IntKey.OverviewResCritical,
        IntKey.OverviewBattWarning,
        IntKey.OverviewBattCritical,
        IntKey.OverviewBageWarning,
        IntKey.OverviewBageCritical,
        OverviewIntentKey.CopyStatusLightsFromNS
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

        // Status Lights subscreen - visibility handled by PreferenceVisibility on each key
        PreferenceSubScreen(
            key = "statuslights_overview_advanced",
            titleResId = app.aaps.core.ui.R.string.statuslights,
            keys = statusLightsKeys
        ) { _ ->
            val activityContext = LocalContext.current
            AdaptivePreferenceList(
                keys = statusLightsKeys.dropLast(1) + listOf(
                    OverviewIntentKey.CopyStatusLightsFromNS.withClick { nsSettingStatus.copyStatusLightsNsSettings(activityContext) }
                ),
                preferences = preferences,
                config = config,
                visibilityContext = visibilityContext
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
