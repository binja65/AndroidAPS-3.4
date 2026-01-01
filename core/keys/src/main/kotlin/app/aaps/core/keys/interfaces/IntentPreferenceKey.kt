package app.aaps.core.keys.interfaces

interface IntentPreferenceKey : PreferenceKey {

    /**
     * String resource ID for URL (for PreferenceType.URL).
     * If set, the URL will be resolved at runtime using stringResource().
     */
    val urlResId: Int?
        get() = null

    /**
     * Activity class to launch (for PreferenceType.ACTIVITY).
     * If set, clicking the preference will launch this activity.
     */
    val activityClass: Class<*>?
        get() = null
}