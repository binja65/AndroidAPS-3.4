package app.aaps.plugins.cgm.eversense

import javax.inject.Inject
import app.aaps.R
import app.aaps.common.helpers.ResourceHelper
import app.aaps.common.logger.AAPSLogger
import app.aaps.common.enums.PluginType
import app.aaps.common.objects.PluginDescription
import app.aaps.plugins.cgm.common.AbstractBgSourceWithSensorInsertLogPlugin
import app.aaps.common.interfaces.BgSource

class EversenseSource @Inject constructor(
    rh: ResourceHelper,
    aapsLogger: AAPSLogger
) : AbstractBgSourceWithSensorInsertLogPlugin(
    PluginDescription()
        .mainType(PluginType.BGSOURCE)
        .fragmentClass(EversenseFragment::class.java.name) // <--- Your Fragment
        .pluginIcon(R.drawable.ic_blooddrop_48) // Use generic icon for now
        .preferencesId(R.xml.eversense_preferences) // <--- Your Menu XML
        .pluginName(R.string.source_eversense)
        .preferencesVisibleInSimpleMode(false)
        .description(R.string.description_source_eversense),
    aapsLogger, rh
), BgSource {

    // Since we inherit from AbstractBgSource..., we might need to implement
    // a few internal methods. If "class EversenseSource" has a red underline,
    // click it and press Alt+Enter > "Implement Members".

    // Likely required member (example):
    // override fun getBgSourceType(): BgSourceType = BgSourceType.XMITTER
}