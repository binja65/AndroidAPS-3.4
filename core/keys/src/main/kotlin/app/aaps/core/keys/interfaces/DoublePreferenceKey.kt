package app.aaps.core.keys.interfaces

interface DoublePreferenceKey : PreferenceKey, DoubleNonPreferenceKey {

    /**
     * Default value if not changed from preferences
     */
    override val defaultValue: Double

    /**
     *  Minimal allowed value
     */
    val min: Double

    /**
     *  Maximal allowed value
     */
    val max: Double

    /**
     *  Value with calculation in simple mode
     */
    val calculatedBySM: Boolean

    /**
     * String resource ID for formatted unit display.
     * Use formatted strings like "units_format_insulin_range" for value with range,
     * or "units_format_insulin" for value only.
     * The format string receives parameters: (value, min, max).
     * If not specified, no unit display.
     */
    val unitsResId: Int?
        get() = null
}