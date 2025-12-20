package app.aaps.plugins.cgm.eversense

import javax.inject.Inject
import app.aaps.R
import app.aaps.common.helpers.ResourceHelper
import app.aaps.common.logger.AAPSLogger
import app.aaps.common.enums.PluginType
import app.aaps.common.objects.PluginDescription
import app.aaps.core.data.plugin.PluginType
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.plugins.source.AbstractBgSourceWithSensorInsertLogPlugin // <--- Correct path for your version
import app.aaps.core.interfaces.source.BgSource // <--- Correct interface path

class EversenseSource @Inject constructor(
    rh: ResourceHelper,
    aapsLogger: AAPSLogger
) : AbstractBgSourceWithSensorInsertLogPlugin(
    PluginDescription()
        .mainType(PluginType.BGSOURCE)
        .fragmentClass(EversenseFragment::class.java.name)
        .pluginIcon(R.drawable.ic_blooddrop_48)
        .preferencesId(R.xml.eversense_preferences)
        .pluginName(R.string.source_eversense)
        .preferencesVisibleInSimpleMode(false)
        .description(R.string.description_source_eversense),
    aapsLogger, rh
), BgSource {

    // If "BgSource" interface requires specific methods, implement them here.
    // For now, this minimal setup should compile if AbstractBgSource handles the basics.
}