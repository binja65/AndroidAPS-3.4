package app.aaps.plugins.cgm.eversense

import javax.inject.Inject
import app.aaps.R
// Fix: Use correct core imports
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.source.BgSource
import app.aaps.plugins.source.AbstractBgSourceWithSensorInsertLogPlugin
// Fix: This is the correct location for PluginType in your version
import app.aaps.core.data.plugin.PluginType

class EversenseSource @Inject constructor(
    rh: ResourceHelper,
    aapsLogger: AAPSLogger
) : AbstractBgSourceWithSensorInsertLogPlugin(
    PluginDescription()
        .mainType(PluginType.BGSOURCE)
        .fragmentClass(EversenseFragment::class.java.name)
        // Fix: Explicitly point to the core object R file
        .pluginIcon(app.aaps.core.objects.R.drawable.ic_blooddrop_48)
        .preferencesId(R.xml.eversense_preferences)
        .pluginName(R.string.source_eversense)
        .preferencesVisibleInSimpleMode(false)
        .description(R.string.description_source_eversense),
    aapsLogger, rh
), BgSource {
    // No extra overrides needed; the abstract class handles defaults.
}