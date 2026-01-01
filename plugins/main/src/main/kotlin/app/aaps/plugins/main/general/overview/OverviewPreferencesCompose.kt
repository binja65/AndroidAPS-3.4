package app.aaps.plugins.main.general.overview

import android.content.Context
import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.nsclient.NSSettingsStatus
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.sharedPreferences.SP
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.DoubleKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.IntentKey
import app.aaps.core.keys.UnitDoubleKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveDoublePreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveDynamicActivityPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveUnitDoublePreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.main.R

/**
 * Compose implementation of Overview preferences.
 */
class OverviewPreferencesCompose(
    private val sp: SP,
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

    // Main content shown at top level
    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Keep Screen On
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.OverviewKeepScreenOn,
            titleResId = R.string.keep_screen_on_title,
            summaryResId = R.string.keep_screen_on_summary
        )

        // QuickWizard Settings
        quickWizardListActivity?.let { activityClass ->
            AdaptiveDynamicActivityPreferenceItem(
                preferences = preferences,
                intentKey = IntentKey.OverviewQuickWizardSettings,
                titleResId = R.string.quickwizard_settings,
                activityClass = activityClass
            )
        }

        // Short tab titles
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.OverviewShortTabTitles,
            titleResId = R.string.short_tabtitles
        )

        // Show notes in dialogs
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.OverviewShowNotesInDialogs,
            titleResId = R.string.overview_show_notes_field_in_dialogs_title
        )

        // Bolus percentage
        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.OverviewBolusPercentage,
            titleResId = app.aaps.core.ui.R.string.partialboluswizard
        )

        AdaptiveIntPreferenceItem(
            preferences = preferences,
            config = config,
            intKey = IntKey.OverviewResetBolusPercentageTime,
            titleResId = app.aaps.core.ui.R.string.partialboluswizard_reset_time
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.OverviewUseBolusAdvisor,
            titleResId = R.string.enable_bolus_advisor,
            summaryResId = R.string.enable_bolus_advisor_summary
        )

        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.OverviewUseBolusReminder,
            titleResId = R.string.enablebolusreminder,
            summaryResId = R.string.enablebolusreminder_summary
        )
    }

    override val subscreens: List<PreferenceSubScreen> = listOf(
        // Buttons Settings subscreen
        PreferenceSubScreen(
            key = "overview_buttons_settings",
            titleResId = R.string.overview_buttons_selection,
            summaryItems = listOf(
                app.aaps.core.ui.R.string.treatments,
                R.string.calculator_label,
                app.aaps.core.ui.R.string.configbuilder_insulin,
                app.aaps.core.ui.R.string.carbs
            )
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.OverviewShowTreatmentButton,
                titleResId = app.aaps.core.ui.R.string.treatments
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.OverviewShowWizardButton,
                titleResId = R.string.calculator_label
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.OverviewShowInsulinButton,
                titleResId = app.aaps.core.ui.R.string.configbuilder_insulin
            )

            // Insulin increment buttons
            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.OverviewInsulinButtonIncrement1,
                titleResId = R.string.firstinsulinincrement
            )

            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.OverviewInsulinButtonIncrement2,
                titleResId = R.string.secondinsulinincrement
            )

            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.OverviewInsulinButtonIncrement3,
                titleResId = R.string.thirdinsulinincrement
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.OverviewShowCarbsButton,
                titleResId = app.aaps.core.ui.R.string.carbs
            )

            // Carbs increment buttons
            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewCarbsButtonIncrement1,
                titleResId = R.string.firstcarbsincrement
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewCarbsButtonIncrement2,
                titleResId = R.string.secondcarbsincrement
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewCarbsButtonIncrement3,
                titleResId = R.string.thirdcarbsincrement
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.OverviewShowCgmButton,
                titleResId = R.string.cgm,
                summaryResId = R.string.show_cgm_button_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.OverviewShowCalibrationButton,
                titleResId = app.aaps.core.ui.R.string.calibration,
                summaryResId = R.string.show_calibration_button_summary
            )
        },

        // Default Temp Targets subscreen
        PreferenceSubScreen(
            key = "default_temp_targets_settings",
            titleResId = R.string.default_temptargets,
            summaryItems = listOf(
                R.string.eatingsoon_duration,
                R.string.activity_duration,
                R.string.hypo_duration
            )
        ) { _ ->
            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewEatingSoonDuration,
                titleResId = R.string.eatingsoon_duration
            )

            AdaptiveUnitDoublePreferenceItem(
                preferences = preferences,
                config = config,
                profileUtil = profileUtil,
                unitKey = UnitDoubleKey.OverviewEatingSoonTarget,
                titleResId = R.string.eatingsoon_target
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewActivityDuration,
                titleResId = R.string.activity_duration
            )

            AdaptiveUnitDoublePreferenceItem(
                preferences = preferences,
                config = config,
                profileUtil = profileUtil,
                unitKey = UnitDoubleKey.OverviewActivityTarget,
                titleResId = R.string.activity_target
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewHypoDuration,
                titleResId = R.string.hypo_duration
            )

            AdaptiveUnitDoublePreferenceItem(
                preferences = preferences,
                config = config,
                profileUtil = profileUtil,
                unitKey = UnitDoubleKey.OverviewHypoTarget,
                titleResId = R.string.hypo_target
            )
        },

        // Prime/Fill Settings subscreen
        PreferenceSubScreen(
            key = "prime_fill_settings",
            titleResId = R.string.fill_bolus_title,
            summaryItems = listOf(
                R.string.button1,
                R.string.button2,
                R.string.button3
            )
        ) { _ ->
            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.ActionsFillButton1,
                titleResId = R.string.button1
            )

            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.ActionsFillButton2,
                titleResId = R.string.button2
            )

            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = DoubleKey.ActionsFillButton3,
                titleResId = R.string.button3
            )
        },

        // Range Settings subscreen
        PreferenceSubScreen(
            key = "range_settings",
            titleResId = R.string.prefs_range_title,
            summaryItems = listOf(
                R.string.low_mark,
                R.string.high_mark
            )
        ) { _ ->
            AdaptiveUnitDoublePreferenceItem(
                preferences = preferences,
                config = config,
                profileUtil = profileUtil,
                unitKey = UnitDoubleKey.OverviewLowMark,
                titleResId = R.string.low_mark
            )

            AdaptiveUnitDoublePreferenceItem(
                preferences = preferences,
                config = config,
                profileUtil = profileUtil,
                unitKey = UnitDoubleKey.OverviewHighMark,
                titleResId = R.string.high_mark
            )
        },

        // Status Lights subscreen
        PreferenceSubScreen(
            key = "statuslights_overview_advanced",
            titleResId = app.aaps.core.ui.R.string.statuslights,
            summaryItems = listOf(
                R.string.show_statuslights,
                R.string.statuslights_cage_warning,
                R.string.statuslights_sage_warning
            )
        ) { _ ->
            val pump = activePlugin.activePump
            val isPatchPump = pump.pumpDescription.isPatchPump

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.OverviewShowStatusLights,
                titleResId = R.string.show_statuslights
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewCageWarning,
                titleResId = R.string.statuslights_cage_warning
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewCageCritical,
                titleResId = R.string.statuslights_cage_critical
            )

            // Only show insulin age for non-patch pumps
            if (!isPatchPump) {
                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = IntKey.OverviewIageWarning,
                    titleResId = R.string.statuslights_iage_warning
                )

                AdaptiveIntPreferenceItem(
                    preferences = preferences,
                    config = config,
                    intKey = IntKey.OverviewIageCritical,
                    titleResId = R.string.statuslights_iage_critical
                )
            }

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewSageWarning,
                titleResId = R.string.statuslights_sage_warning
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewSageCritical,
                titleResId = R.string.statuslights_sage_critical
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewSbatWarning,
                titleResId = R.string.statuslights_sbat_warning
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewSbatCritical,
                titleResId = R.string.statuslights_sbat_critical
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewResWarning,
                titleResId = R.string.statuslights_res_warning
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewResCritical,
                titleResId = R.string.statuslights_res_critical
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewBattWarning,
                titleResId = R.string.statuslights_bat_warning
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewBattCritical,
                titleResId = R.string.statuslights_bat_critical
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewBageWarning,
                titleResId = R.string.statuslights_bage_warning
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.OverviewBageCritical,
                titleResId = R.string.statuslights_bage_critical
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
            summaryItems = listOf(R.string.enablesuperbolus)
        ) { _ ->
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.OverviewUseSuperBolus,
                titleResId = R.string.enablesuperbolus,
                summaryResId = R.string.enablesuperbolus_summary
            )
        }
    )
}
