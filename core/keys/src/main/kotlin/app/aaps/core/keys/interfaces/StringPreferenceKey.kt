package app.aaps.core.keys.interfaces

interface StringPreferenceKey : PreferenceKey, StringNonPreferenceKey {

    /**
     * Default value if not changed from preferences
     */
    override val defaultValue: String
    val isPassword: Boolean
    val isPin: Boolean

    /**
     * Entries for LIST type preferences.
     * Map of stored value -> label resource ID.
     * Empty map means no entries (not a list preference).
     */
    val entries: Map<String, Int>
        get() = emptyMap()
}