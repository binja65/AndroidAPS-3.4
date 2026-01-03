package app.aaps.core.keys.interfaces

/**
 * Context provided to [PreferenceVisibility] lambdas for evaluating
 * whether a preference should be visible.
 *
 * This interface abstracts away the need for preferences to know about
 * specific plugin implementations while still allowing runtime visibility decisions.
 */
interface PreferenceVisibilityContext {

    /**
     * Whether the active pump is a patch pump (like Omnipod).
     * Patch pumps don't have replaceable insulin cartridges, so insulin age preferences are hidden.
     */
    val isPatchPump: Boolean

    /**
     * Whether the active pump has a replaceable battery.
     */
    val isBatteryReplaceable: Boolean

    /**
     * Whether the active pump logs battery changes (even if battery isn't user-replaceable).
     */
    val isBatteryChangeLoggingEnabled: Boolean

    /**
     * Whether the active BG source supports advanced filtering (for SMB-related preferences).
     */
    val advancedFilteringSupported: Boolean

    /**
     * Access to preferences for checking other preference values.
     * Useful for dependent visibility (e.g., show PIN field only if protection type is PIN).
     */
    val preferences: Preferences

    /**
     * Whether the pump is currently paired/connected.
     * Used by pump plugins like ComboV2 to enable/disable pairing preferences.
     */
    val isPumpPaired: Boolean
        get() = false

    /**
     * Whether the pump is initialized and ready for operation.
     * Used by pump plugins like Medtrum to disable serial input after initialization.
     */
    val isPumpInitialized: Boolean
        get() = false
}
