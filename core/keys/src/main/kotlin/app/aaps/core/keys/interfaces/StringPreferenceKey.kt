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

    /**
     * Runtime-resolved entries for LIST type preferences.
     * Map of stored value -> resolved label string.
     * When set, this takes precedence over [entries] resource IDs.
     */
    val resolvedEntries: Map<String, String>?
        get() = null

    /**
     * Validator for the string value.
     * Used to validate input before accepting it.
     * Default is no validation.
     */
    val validator: StringValidator
        get() = StringValidator.NONE
}

/**
 * Wrapper that attaches runtime-resolved entries to a StringPreferenceKey.
 * Uses delegation to preserve all other properties from the original key.
 */
class StringKeyWithEntries(
    private val delegate: StringPreferenceKey,
    override val resolvedEntries: Map<String, String>
) : StringPreferenceKey by delegate

/**
 * Creates a new StringPreferenceKey with runtime-resolved entries attached.
 * Use this when entries need to be resolved at runtime (e.g., from plugins).
 *
 * @param entries Map of stored value -> resolved label string
 * @return A new StringPreferenceKey with the entries attached
 */
fun StringPreferenceKey.withEntries(entries: Map<String, String>): StringPreferenceKey =
    StringKeyWithEntries(this, entries)