package app.aaps.core.keys.interfaces

interface IntPreferenceKey : PreferenceKey, IntNonPreferenceKey {

    /**
     * Default value if not changed from preferences
     */
    override val defaultValue: Int

    /**
     *  Minimal allowed value
     */
    val min: Int

    /**
     *  Maximal allowed value
     */
    val max: Int

    /**
     *  Value with calculation of default value
     */
    val calculatedDefaultValue: Boolean

    /**
     * Visible in engineering mode only, otherwise `defaultValue`
     */
    val engineeringModeOnly: Boolean

    /**
     * Entries for LIST type preferences.
     * Map of stored value -> label resource ID.
     * Empty map means no entries (not a list preference).
     */
    val entries: Map<Int, Int>
        get() = emptyMap()
}