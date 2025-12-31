package app.aaps.plugins.source

import android.content.Context
import androidx.compose.foundation.lazy.LazyListScope
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.plugin.PluginBase
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.source.BgSource
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.validators.preferences.AdaptiveSwitchPreference

abstract class AbstractBgSourceWithSensorInsertLogPlugin(
    pluginDescription: PluginDescription,
    aapsLogger: AAPSLogger,
    rh: ResourceHelper,
    private val preferences: Preferences,
    private val config: Config
) : PluginBase(pluginDescription, aapsLogger, rh), BgSource {

    override fun addPreferenceScreen(preferenceManager: PreferenceManager, parent: PreferenceScreen, context: Context, requiredKey: String?) {
        if (requiredKey != null) return
        val category = PreferenceCategory(context)
        parent.addPreference(category)
        category.apply {
            key = "bg_source_with_sensor_upload_settings"
            title = rh.gs(R.string.bgsource_settings)
            initialExpandedChildrenCount = 0
            addPreference(AdaptiveSwitchPreference(ctx = context, booleanKey = BooleanKey.BgSourceUploadToNs, title = app.aaps.core.ui.R.string.do_ns_upload_title))
            addPreference(AdaptiveSwitchPreference(ctx = context, booleanKey = BooleanKey.BgSourceCreateSensorChange, summary = R.string.bgsource_log_sensor_change_summary, title = R.string.bgsource_log_sensor_change_title))
        }
    }

    override fun getPreferenceScreenContent(): Any = AbstractBgSourceWithSensorPreferencesCompose(preferences, config, name)

    private inner class AbstractBgSourceWithSensorPreferencesCompose(
        private val preferences: Preferences,
        private val config: Config,
        private val pluginName: String
    ) : PreferenceScreenContent {

        override val keyPrefix: String
            get() = "AbstractBgSourceWithSensor_$pluginName"

        override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
            val sectionKey = "${keyPrefix}_bg_source_with_sensor_upload_settings"
            item {
                val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
                CollapsibleCardSectionContent(
                    titleResId = R.string.bgsource_settings,
                    summaryItems = listOf(
                        app.aaps.core.ui.R.string.do_ns_upload_title,
                        R.string.bgsource_log_sensor_change_title
                    ),
                    expanded = isExpanded,
                    onToggle = { sectionState?.toggle(sectionKey) }
                ) {
                    AdaptiveSwitchPreferenceItem(
                        preferences = preferences,
                        config = config,
                        booleanKey = BooleanKey.BgSourceUploadToNs,
                        titleResId = app.aaps.core.ui.R.string.do_ns_upload_title
                    )

                    AdaptiveSwitchPreferenceItem(
                        preferences = preferences,
                        config = config,
                        booleanKey = BooleanKey.BgSourceCreateSensorChange,
                        titleResId = R.string.bgsource_log_sensor_change_title,
                        summaryResId = R.string.bgsource_log_sensor_change_summary
                    )
                }
            }
        }
    }
}
