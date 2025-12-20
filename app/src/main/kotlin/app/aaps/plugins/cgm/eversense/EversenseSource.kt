package app.aaps.plugins.cgm.eversense

import javax.inject.Inject // <--- Make sure this is imported
import app.aaps.common.interfaces.PluginBase
import app.aaps.plugins.cgm.common.CgmSourcePlugin

// Add "@Inject constructor()" here:
class EversenseSource @Inject constructor() : CgmSourcePlugin, PluginBase { 

    override fun getName(): String = "Eversense"

    // Shows in the "General" section (Configuration list)
    override fun isEnabled(type: Int): Boolean = type == PluginBase.GENERAL

    // The name the user actually sees in the list
    override fun getVisibleName(): String = "Eversense"

    // Allows the user to hide it
    override fun canBeHidden(): Boolean = true

    // This connects the "Gear" icon to the menu below
    override fun getSettingsFragment(): Any {
        return EversenseFragment()
    }
}