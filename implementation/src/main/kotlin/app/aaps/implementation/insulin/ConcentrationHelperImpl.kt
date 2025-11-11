package app.aaps.implementation.insulin

import app.aaps.core.interfaces.insulin.ConcentrationHelper
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.interfaces.profile.EffectiveProfile
import app.aaps.core.interfaces.profile.Profile
import app.aaps.core.interfaces.profile.ProfileFunction
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.utils.DecimalFormatter
import app.aaps.core.keys.IntNonKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.implementation.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConcentrationHelperImpl @Inject constructor(
    val aapsLogger: AAPSLogger,
    private val activePlugin: ActivePlugin,
    private val profileFunction: ProfileFunction,
    private val rh: ResourceHelper,
    private val preferences: Preferences,
    private val decimalFormatter: DecimalFormatter
) : ConcentrationHelper {

    override fun isU100(): Boolean = concentration == 1.0

    override fun toPump(amount: Double): Double = amount / concentration

    override fun fromPump(amount: Double): Double = amount * concentration

    override fun toPump(profile: EffectiveProfile): Profile = profile.toPump()

    override fun basalRateString(rate: Double): String {
        if (isU100())
            return rh.gs(app.aaps.core.ui.R.string.pump_base_basal_rate, rate)
        else {
            val iUString = rh.gs(app.aaps.core.ui.R.string.pump_base_basal_rate, fromPump(rate))
            val cUString = rh.gs(R.string.pump_base_basal_rate_cu, rate)
            return rh.gs(R.string.concentration_format, iUString, cUString)
        }
    }

    override fun insulinAmountString(amount: Double): String {
        if (isU100())
            return decimalFormatter.toPumpSupportedBolusWithUnits(amount, activePlugin.activePump.pumpDescription.bolusStep)
        else { // app.aaps.core.ui.R.string.format_insulin_units
            val iUString = decimalFormatter.toPumpSupportedBolusWithUnits(fromPump(amount), fromPump(activePlugin.activePump.pumpDescription.bolusStep))
            val cUString = decimalFormatter.toPumpSupportedBolusWithConcentratedUnits(amount, activePlugin.activePump.pumpDescription.bolusStep)
            return rh.gs(R.string.concentration_format, iUString, cUString)
        }
    }

    override fun insulinConcentrationString(): String = rh.gs(R.string.insulin_concentration, preferences.get(IntNonKey.InsulinConcentration))

    override fun bolusWithVolume(amount: Double): String = rh.gs(
        R.string.bolus_with_volume,
        decimalFormatter.toPumpSupportedBolus(amount, activePlugin.activePump.pumpDescription.bolusStep),
        amount * 10
    )

    override fun bolusWithConvertedVolume(amount: Double): String = rh.gs(
        R.string.bolus_with_volume,
        decimalFormatter.toPumpSupportedBolus(amount, activePlugin.activePump.pumpDescription.bolusStep),
        toPump(amount * 10)
    )

    override fun bolusProgressShort(delivered: Double, totalAmount: Double): String {
        if (isU100())
            return rh.gs(R.string.bolus_delivered, delivered, totalAmount)
        else {
            val amountString = rh.gs(R.string.bolus_delivered_CU, delivered, totalAmount)
            val convertedString = rh.gs(R.string.bolus_delivered, fromPump(delivered), fromPump(totalAmount))
            return rh.gs(R.string.concentration_format, convertedString, amountString)
        }
    }

    /**
     * Todo: revue provided value according to Safety (Approved value or profileFunction.getProfile()?.iCfg?.concentration ?:1.0
     */
    override val concentration: Double
        get() = preferences.get(IntNonKey.InsulinConcentration) / 100.0

}