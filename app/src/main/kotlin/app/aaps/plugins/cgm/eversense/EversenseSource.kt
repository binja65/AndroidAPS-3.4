package app.aaps.plugins.cgm.eversense

import javax.inject.Inject
import app.aaps.R
import app.aaps.common.enums.PluginType
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.source.BgSource
import app.aaps.plugins.source.AbstractBgSourceWithSensorInsertLogPlugin

class EversenseSource @Inject constructor(
    rh: ResourceHelper,
    aapsLogger: AAPSLogger
) : AbstractBgSourceWithSensorInsertLogPlugin(
    PluginDescription()
        .mainType(PluginType.BGSOURCE)
        .fragmentClass(EversenseFragment::class.java.name)
        // This specific path was found in your XdripSourcePlugin snippet
        .pluginIcon(app.aaps.core.objects.R.drawable.ic_blooddrop_48)
        .preferencesId(R.xml.eversense_preferences)
        .pluginName(R.string.source_eversense)
        .preferencesVisibleInSimpleMode(false)
        .description(R.string.description_source_eversense),
    aapsLogger, rh
), BgSource {

    // If you get a red line here saying "Implement members",
    // click inside the class, press Alt+Enter, and select "Implement members".
}