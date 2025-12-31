package app.aaps.plugins.main.general.overview

import android.content.Context
import androidx.compose.foundation.lazy.LazyListScope
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
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.UnitDoubleKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveDoublePreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveDynamicActivityPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveUnitDoublePreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
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
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Overview category
        val overviewSettingsKey = "${keyPrefix}_overview_settings"
        item {
            val isExpanded = sectionState?.isExpanded(overviewSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.overview,
                summaryItems = listOf(
                    R.string.keep_screen_on_title,
                    R.string.quickwizard_settings,
                    R.string.short_tabtitles,
                    R.string.overview_show_notes_field_in_dialogs_title
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(overviewSettingsKey) }
            ) {
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
            }
        }

        // Buttons Settings Category
        val buttonsSettingsKey = "${keyPrefix}_overview_buttons_settings"
        item {
            val isExpanded = sectionState?.isExpanded(buttonsSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.overview_buttons_selection,
                summaryItems = listOf(
                    app.aaps.core.ui.R.string.treatments,
                    R.string.calculator_label,
                    app.aaps.core.ui.R.string.configbuilder_insulin,
                    app.aaps.core.ui.R.string.carbs,
                    R.string.cgm
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(buttonsSettingsKey) }
            ) {
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
            }
        }

        // Default Temp Targets Settings
        val tempTargetsSettingsKey = "${keyPrefix}_default_temp_targets_settings"
        item {
            val isExpanded = sectionState?.isExpanded(tempTargetsSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.default_temptargets,
                summaryItems = listOf(
                    R.string.eatingsoon_duration,
                    R.string.activity_duration,
                    R.string.hypo_duration
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(tempTargetsSettingsKey) }
            ) {
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
            }
        }

        // Prime/Fill Settings
        val primeFillSettingsKey = "${keyPrefix}_prime_fill_settings"
        item {
            val isExpanded = sectionState?.isExpanded(primeFillSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.fill_bolus_title,
                summaryItems = listOf(
                    R.string.button1,
                    R.string.button2,
                    R.string.button3
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(primeFillSettingsKey) }
            ) {
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
            }
        }

        // Range Settings
        val rangeSettingsKey = "${keyPrefix}_range_settings"
        item {
            val isExpanded = sectionState?.isExpanded(rangeSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.prefs_range_title,
                summaryItems = listOf(
                    R.string.low_mark,
                    R.string.high_mark
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(rangeSettingsKey) }
            ) {
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
            }
        }

        // Status Lights Category
        val statusLightsKey = "${keyPrefix}_statuslights_overview_advanced"
        item {
            val isExpanded = sectionState?.isExpanded(statusLightsKey) ?: true
            val pump = activePlugin.activePump
            val isPatchPump = pump.pumpDescription.isPatchPump

            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.statuslights,
                summaryItems = listOf(
                    R.string.show_statuslights,
                    R.string.statuslights_cage_warning,
                    R.string.statuslights_sage_warning
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(statusLightsKey) }
            ) {
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
            }
        }

        // Bolus Settings
        val bolusSettingsKey = "${keyPrefix}_overview_bolus_settings"
        item {
            val isExpanded = sectionState?.isExpanded(bolusSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.bolus,
                summaryItems = listOf(
                    app.aaps.core.ui.R.string.partialboluswizard,
                    R.string.enable_bolus_advisor,
                    R.string.enablebolusreminder
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(bolusSettingsKey) }
            ) {
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
        }

        // Advanced Settings Category
        val advancedSettingsKey = "${keyPrefix}_overview_advanced_settings"
        item {
            val isExpanded = sectionState?.isExpanded(advancedSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = app.aaps.core.ui.R.string.advanced_settings_title,
                summaryItems = listOf(R.string.enablesuperbolus),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(advancedSettingsKey) }
            ) {
                AdaptiveSwitchPreferenceItem(
                    preferences = preferences,
                    config = config,
                    booleanKey = BooleanKey.OverviewUseSuperBolus,
                    titleResId = R.string.enablesuperbolus,
                    summaryResId = R.string.enablesuperbolus_summary
                )
            }
        }
    }
}
