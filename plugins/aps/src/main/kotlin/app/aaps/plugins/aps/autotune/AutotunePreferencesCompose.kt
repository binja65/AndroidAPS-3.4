package app.aaps.plugins.aps.autotune

import androidx.compose.foundation.lazy.LazyListScope
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveIntPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.plugins.aps.R

/**
 * Compose implementation of Autotune preferences.
 */
class AutotunePreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : PreferenceScreenContent {

    override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
        // Autotune settings category
        val autotuneSettingsKey = "${keyPrefix}_autotune_settings"
        item {
            val isExpanded = sectionState?.isExpanded(autotuneSettingsKey) ?: true
            CollapsibleCardSectionContent(
                titleResId = R.string.autotune_settings,
                summaryItems = listOf(
                    R.string.autotune_auto_title,
                    R.string.autotune_categorize_uam_as_basal_title,
                    R.string.autotune_default_tune_days_title,
                    R.string.autotune_circadian_ic_isf_title
                ),
                expanded = isExpanded,
                onToggle = { sectionState?.toggle(autotuneSettingsKey) }
            ) {
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.AutotuneAutoSwitchProfile,
                titleResId = R.string.autotune_auto_title,
                summaryResId = R.string.autotune_auto_summary
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.AutotuneCategorizeUamAsBasal,
                titleResId = R.string.autotune_categorize_uam_as_basal_title,
                summaryResId = R.string.autotune_categorize_uam_as_basal_summary
            )

            AdaptiveIntPreferenceItem(
                preferences = preferences,
                config = config,
                intKey = IntKey.AutotuneDefaultTuneDays,
                titleResId = R.string.autotune_default_tune_days_title
            )

            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = BooleanKey.AutotuneCircadianIcIsf,
                titleResId = R.string.autotune_circadian_ic_isf_title,
                summaryResId = R.string.autotune_circadian_ic_isf_summary
            )
            }
        }
    }
}
