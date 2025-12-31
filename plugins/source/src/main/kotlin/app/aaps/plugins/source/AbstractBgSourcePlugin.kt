package app.aaps.plugins.source

import android.content.Context
import androidx.compose.foundation.lazy.LazyListScope
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.plugin.PluginBaseWithPreferences
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.source.BgSource
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.NonPreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.CollapsibleCardSectionContent
import app.aaps.core.ui.compose.preference.PreferenceScreenContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.validators.preferences.AdaptiveSwitchPreference

abstract class AbstractBgSourcePlugin(
    pluginDescription: PluginDescription,
    ownPreferences: List<Class<out NonPreferenceKey>> = emptyList(),
    aapsLogger: AAPSLogger,
    rh: ResourceHelper,
    preferences: Preferences,
    private val config: Config
) : PluginBaseWithPreferences(pluginDescription, ownPreferences, aapsLogger, rh, preferences), BgSource {

    override fun addPreferenceScreen(preferenceManager: PreferenceManager, parent: PreferenceScreen, context: Context, requiredKey: String?) {
        if (requiredKey != null) return
        val category = PreferenceCategory(context)
        parent.addPreference(category)
        category.apply {
            key = "bg_source_upload_settings"
            title = rh.gs(R.string.bgsource_settings)
            initialExpandedChildrenCount = 0
            addPreference(AdaptiveSwitchPreference(ctx = context, booleanKey = BooleanKey.BgSourceUploadToNs, title = app.aaps.core.ui.R.string.do_ns_upload_title))
        }
    }

    override fun getPreferenceScreenContent(): Any = AbstractBgSourcePreferencesCompose(preferences, config, name)

    private inner class AbstractBgSourcePreferencesCompose(
        private val preferences: Preferences,
        private val config: Config,
        private val pluginName: String
    ) : PreferenceScreenContent {

        override val keyPrefix: String
            get() = "AbstractBgSource_$pluginName"

        override fun LazyListScope.preferenceItems(sectionState: PreferenceSectionState?) {
            val sectionKey = "${keyPrefix}_bg_source_upload_settings"
            item {
                val isExpanded = sectionState?.isExpanded(sectionKey) ?: true
                CollapsibleCardSectionContent(
                    titleResId = R.string.bgsource_settings,
                    expanded = isExpanded,
                    onToggle = { sectionState?.toggle(sectionKey) }
                ) {
                    AdaptiveSwitchPreferenceItem(
                        preferences = preferences,
                        config = config,
                        booleanKey = BooleanKey.BgSourceUploadToNs,
                        titleResId = app.aaps.core.ui.R.string.do_ns_upload_title
                    )
                }
            }
        }
    }
}
