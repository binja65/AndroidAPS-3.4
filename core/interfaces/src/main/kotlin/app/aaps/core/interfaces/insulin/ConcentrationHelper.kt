package app.aaps.core.interfaces.insulin

import app.aaps.core.interfaces.profile.EffectiveProfile
import app.aaps.core.interfaces.profile.Profile

interface ConcentrationHelper {
    /**
     *  return true if default concentration (U100) is set, false if another concentration has been approved by user
     */
    fun isU100(): Boolean

    /**
     * Convert insulin amount sent to pump with current concentration
     * TBC if needed
     */
    fun toPump(amount: Double): Double

    /**
     * Convert concentrated amount received from pump to IU using current concentration
     * TBC if needed
     */
    fun fromPump(amount: Double): Double

    /**
     * Convert EffectiveProfile defined in IU to Profile (in CU) sent to pump with current concentration
     * TBC if needed
     */
    fun toPump(profile: EffectiveProfile): Profile

    /** Get Current Profile with concentration convertion (to use within Pump Driver)
     * TBC if needed
     */
    fun getProfile(): Profile?

    /**
     * basalrate with units in U/h if U100
     * i.e. "0.6 U/h", and with both value if other concentration: i.e. for U200 "0.6 U/h (0.3 CU/h)"
     *
     * @param rate absolute rate in IU or in CU
     * @param toPump if false (default) rate is CU, if true rate is IU
     * @return String with units (U100) or with both units if not U100
     */
    fun basalRateString(rate: Double, toPump: Boolean = false): String

    /** show bolus with units in U if U100
     * i.e. "4 U", and with both value if other concentration: i.e. for U200 "4 U (2 U)"
     *
     * @param amount bolus amount in IU or in CU
     * @param toPump if false (default) amount is CU, if true amount is IU
     * @return String with units (U100) or with both units if not U100
     */
    fun insulinAmountString(amount: Double, toPump: Boolean = false): String

    /**
     * show insulinConcentration as a String i.e. "U100", "U200", ...
     */
    fun insulinConcentrationString(): String

    /** show bolus with volume in µl
     * Dedicated to Prime/Fill Dialog (to show volume of fluid delivered)
     * i.e. "0.7 U (7.0 µl)"
     *
     * @param amount insulin amount in IU
     */
    fun bolusWithVolume(amount:Double): String

    /**
     * Show bolus with volume in µl after convertion due to concentration
     * Dedicated to Prime/Fill Dialog (to show volume of fluid delivered)
     * i.e. "1.4 CU (7.0 µl)"
     *
     * @param amount insulin amount in CU
     */
    fun bolusWithConvertedVolume(amount:Double): String

    /**
     * show bolus Progress information with delivered
     * TBC if needed (different presentation was used according to different pump drivers)
     * to be used only within the new EventOverviewBolusProgress
     *
     * again, with U100, no change: i.e. "2.5U / 5.0U delivered" (or "2.5 U delivered")
     * with other concentration: i.e; U200: "2.5U / 5.0U (1.25CU / 2.5CU) delivered" (or "2.5 U (1.25 CU) delivered")
     *
     */
    fun bolusProgress(delivered: Double, totalAmount: Double): String

    /**
     * show bolus Progress information with delivered
     * TBC if needed (different presentation was used according to different pump drivers, here a simplified view could be provided by insulinAmountString(xxx)
     * I saw within your latest refactoring of EventOverviewBolusProgress, you only show the delivered amount
     * to be used only within the new EventOverviewBolusProgress
     *
     * again, with U100, no change: i.e. "2.5U / 5.0U"
     * with other concentration: i.e; U200: "2.5U / 5.0U (1.25CU / 2.5CU)"
     *
     */
    fun bolusProgressShort(delivered: Double, totalAmount: Double): String

    /**
     * Provide current concentration approved by user
     * For Safety and to provide a reminder after Reservoir Change, I stored approved concentration in preferences (but we can of course discuss and change the management)
     * this approved concentration (automatically provided for U100), is used here and not getProfile()?.iCfg?.concentration (which can be null) but replaced by 1.0 by default
     *
     * Use case I had in mind is a fresh install (with no selected profile by default) and a load preference including U200 from a previous phone,
     * but not allowed due to missing concentration_enable file
     * - profileFunction.getProfile()?.iCfg?.concentration will answer 1.0 or null (default values)
     * - using stored value in preferences, I get 2.0 previously approved, but not allowed so I can raise a warning or disable loop until user fix manually the problem
     * (approve U100 or put enable_concentration file)
     */
    val concentration: Double
}