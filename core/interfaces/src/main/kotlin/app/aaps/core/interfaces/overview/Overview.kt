package app.aaps.core.interfaces.overview

import android.widget.TextView
import app.aaps.core.interfaces.configuration.ConfigExportImport
import app.aaps.core.interfaces.rx.bus.RxBus

interface Overview : ConfigExportImport {

    val overviewBus: RxBus

    /**
     * Set textView content with version and warning color
     */
    fun setVersionView(view: TextView)

    /**
     * Apply status lights settings from NS
     * Called by NSSettingsStatus to avoid direct key dependency
     */
    fun applyStatusLightsFromNs(
        cageWarn: Int?, cageCritical: Int?,
        iageWarn: Int?, iageCritical: Int?,
        sageWarn: Int?, sageCritical: Int?,
        bageWarn: Int?, bageCritical: Int?
    )
}