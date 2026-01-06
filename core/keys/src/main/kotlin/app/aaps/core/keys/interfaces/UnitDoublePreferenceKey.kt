package app.aaps.core.keys.interfaces

interface UnitDoublePreferenceKey : PreferenceKey {

    /**
     * Default value if not changed from preferences
     */
    val defaultValue: Double

    /**
     *  Minimal allowed value
     */
    val minMgdl: Int

    /**
     *  Maximal allowed value
     */
    val maxMgdl: Int

    /**
     * String resource ID for formatted unit display.
     * For UnitDoublePreferenceKey, this is typically not used directly
     * as the unit (mg/dL vs mmol/L) is determined dynamically at runtime.
     * The composable will use units_format_mgdl_range or units_format_mmol_range.
     */
    val unitsResId: Int?
        get() = null
}