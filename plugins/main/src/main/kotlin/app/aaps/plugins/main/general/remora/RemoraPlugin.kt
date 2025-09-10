package app.aaps.plugins.main.general.remora

import android.content.Context
import app.aaps.core.data.plugin.PluginType
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.interfaces.plugin.PluginBase
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.rx.AapsSchedulers
import app.aaps.core.interfaces.rx.events.EventUpdateOverviewGraph
import app.aaps.plugins.main.R
import de.tebbeubben.remora.lib.LibraryMode
import de.tebbeubben.remora.lib.RemoraLib
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoraPlugin @Inject constructor(
    aapsLogger: AAPSLogger,
    rh: ResourceHelper,
    context: Context,
    private val statusDataBuilder: StatusDataBuilder,
    val activePlugin: ActivePlugin,
    val aapsSchedulers: AapsSchedulers
) : PluginBase(
    PluginDescription()
        .mainType(PluginType.GENERAL)
        .fragmentClass(RemoraFragment::class.java.name)
        .pluginIcon(de.tebbeubben.remora.lib.R.drawable.remora_logo)
        .pluginName(R.string.remora)
        .shortName(R.string.remora_shortname)
        .description(R.string.description_remora),
    aapsLogger, rh
) {

    private var scope = CoroutineScope(Dispatchers.Default)

    init {
        RemoraLib.initialize(context, LibraryMode.MAIN)
    }

    override fun onStart() {
        scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            RemoraLib.instance.startup()
            withContext(Dispatchers.IO) {
                activePlugin.activeOverview.overviewBus
                    .toObservable(EventUpdateOverviewGraph::class.java)
                    .debounce(1L, TimeUnit.SECONDS)
                    .asFlow()
                    .collectLatest {
                        val statusData = statusDataBuilder.constructStatusData()
                        if (statusData != null) {
                            RemoraLib.instance.shareStatus(statusData)
                        }
                    }
            }

        }
    }

    override fun onStop() {
        scope.cancel()
        runBlocking {
            RemoraLib.instance.shutdown()
        }
    }
}