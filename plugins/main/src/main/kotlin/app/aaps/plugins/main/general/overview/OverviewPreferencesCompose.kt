package app.aaps.plugins.main.general.overview

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.overview.Overview
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.UnitDoubleKey
import app.aaps.core.keys.interfaces.PreferenceItem
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withActivity
import app.aaps.core.keys.interfaces.withClick
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreenDef
import app.aaps.plugins.main.R
import app.aaps.plugins.main.general.overview.keys.OverviewIntKey
import app.aaps.plugins.main.general.overview.keys.OverviewIntentKey

/**
 * Compose implementation of Overview preferences.
 * Uses lightweight PreferenceSubScreenDef with auto-generated content.
 */
class OverviewPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val profileUtil: ProfileUtil,
    private val visibilityContext: PreferenceVisibilityContext,
    quickWizardListActivity: Class<*>? = null,
    private val overview: Overview,
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.overview

    // Unified list with lightweight subscreen definitions
    override val items: List<PreferenceItem> = listOf(
        // Main preferences
        BooleanKey.OverviewKeepScreenOn,
        quickWizardListActivity?.let { OverviewIntentKey.QuickWizardSettings.withActivity(it) } ?: OverviewIntentKey.QuickWizardSettings,
        BooleanKey.OverviewShowNotesInDialogs,

        // Buttons settings subscreen (auto-generated content)
        PreferenceSubScreenDef(
            key = "overview_buttons_settings",
            titleResId = R.string.overview_buttons_selection,
            keys = listOf(
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
        ),

        IntKey.OverviewBolusPercentage,
        IntKey.OverviewResetBolusPercentageTime,
        BooleanKey.OverviewUseBolusAdvisor,
        BooleanKey.OverviewUseBolusReminder,

        // Temp targets subscreen (auto-generated content)
        PreferenceSubScreenDef(
            key = "default_temp_targets_settings",
            titleResId = R.string.default_temptargets,
            keys = listOf(
                IntKey.OverviewEatingSoonDuration,
                UnitDoubleKey.OverviewEatingSoonTarget,
                IntKey.OverviewActivityDuration,
                UnitDoubleKey.OverviewActivityTarget,
                IntKey.OverviewHypoDuration,
                UnitDoubleKey.OverviewHypoTarget
            )
        ),

        // Fill settings subscreen (auto-generated content)
        PreferenceSubScreenDef(
            key = "prime_fill_settings",
            titleResId = R.string.fill_bolus_title,
            keys = listOf(
                DoubleKey.ActionsFillButton1,
                DoubleKey.ActionsFillButton2,
                DoubleKey.ActionsFillButton3
            )
        ),

        // Range settings subscreen (auto-generated content)
        PreferenceSubScreenDef(
            key = "range_settings",
            titleResId = R.string.prefs_range_title,
            keys = listOf(
                UnitDoubleKey.OverviewLowMark,
                UnitDoubleKey.OverviewHighMark
            )
        ),

        // Status lights subscreen (custom content - needs special handling)
        PreferenceSubScreenDef(
            key = "statuslights_overview_advanced",
            titleResId = app.aaps.core.ui.R.string.statuslights,
            keys = listOf(
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
            ),
            customContent = { _ ->
                val activityContext = LocalContext.current
                AdaptivePreferenceList(
                    keys = listOf(
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
                        OverviewIntentKey.CopyStatusLightsFromNS.withClick { overview.applyStatusLightsFromNs(activityContext) }
                    ),
                    preferences = preferences,
                    config = config,
                    visibilityContext = visibilityContext
                )
            }
        ),

        // Advanced settings subscreen (auto-generated content)
        PreferenceSubScreenDef(
            key = "overview_advanced_settings",
            titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
            keys = listOf(BooleanKey.OverviewUseSuperBolus)
        )
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,  // Derived from items.filterIsInstance<PreferenceKey>()
            preferences = preferences,
            config = config,
            profileUtil = profileUtil
        )
    }

    // Auto-generate content for PreferenceSubScreenDef (when customContent is null)
    @Composable
    override fun renderAutoGeneratedContent(def: PreferenceSubScreenDef) {
        AdaptivePreferenceList(
            keys = def.keys,
            preferences = preferences,
            config = config,
            profileUtil = profileUtil
        )
    }
}
