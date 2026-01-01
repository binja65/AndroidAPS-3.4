package app.aaps.plugins.source

import android.content.Context
import android.util.Log
import app.aaps.core.data.model.GV
import app.aaps.core.data.model.SourceSensor
import app.aaps.core.data.model.TrendArrow
import app.aaps.core.data.plugin.PluginType
import app.aaps.core.data.ue.Sources
import app.aaps.core.interfaces.db.PersistenceLayer
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.source.BgSource
import app.aaps.plugins.source.fragments.EversenseFragment
import com.nightscout.eversense.EversenseCGMPlugin
import com.nightscout.eversense.callbacks.EversenseWatcher
import com.nightscout.eversense.enums.EversenseTrendArrow
import com.nightscout.eversense.enums.EversenseType
import java.util.Date
import javax.inject.Inject

class EversensePlugin @Inject constructor(
    rh: ResourceHelper,
    private val context: Context,
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
), BgSource, EversenseWatcher {

    @Inject lateinit var persistenceLayer: PersistenceLayer

    // No extra overrides needed; the abstract class handles defaults.
    init {
        EversenseCGMPlugin.instance.setContext(context)
        EversenseCGMPlugin.instance.addWatcher(this)

        EversenseCGMPlugin.instance.connect(null)
    }

    override fun onCGMRead(type: EversenseType, glucoseInMgDl: Int, datetime: Long, trend: EversenseTrendArrow) {
        Log.w("onCGMRead", "Received glucose data: $glucoseInMgDl mg/dl, time: $datetime, persistenceLayer: ${persistenceLayer == null}")
        val value = GV(
            timestamp = datetime,
            value = glucoseInMgDl.toDouble(),
            noise = null,
            raw = null,
            trendArrow = TrendArrow.fromString(trend.type),
            sourceSensor = when(type) {
                EversenseType.EVERSENSE_365 -> SourceSensor.EVERSENSE_365
                EversenseType.EVERSENSE_E3 -> SourceSensor.EVERSENSE_E3
            }
        )

        val result = persistenceLayer.insertCgmSourceData(
            Sources.Eversense,
            listOf(value),
            listOf(),
            null
        ).blockingGet()

        for (inserted in result.inserted) {
            Log.i("onCGMRead", "Inserted glucose: ${inserted.value} mg/dl")
        }

        for (invalidated in result.invalidated) {
            Log.i("onCGMRead", "invalidated glucose: ${invalidated.value} mg/dl")
        }
    }
}