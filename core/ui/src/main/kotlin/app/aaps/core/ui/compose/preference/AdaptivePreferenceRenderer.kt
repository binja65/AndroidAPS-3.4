/*
 * Adaptive Preference Renderer for Jetpack Compose
 * Provides generic rendering for any PreferenceKey type
 */

package app.aaps.core.ui.compose.preference

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.PreferenceType
import app.aaps.core.keys.interfaces.BooleanPreferenceKey
import app.aaps.core.keys.interfaces.DoublePreferenceKey
import app.aaps.core.keys.interfaces.IntPreferenceKey
import app.aaps.core.keys.interfaces.IntentPreferenceKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.keys.interfaces.StringPreferenceKey
import app.aaps.core.keys.interfaces.UnitDoublePreferenceKey

/**
 * Renders a preference based on its PreferenceKey type and preferenceType.
 * Automatically selects the appropriate composable.
 *
 * For LIST types, loads entries from resources using entriesResId/entryValuesResId.
 * For URL/ACTIVITY types on IntentPreferenceKey, requires additional parameters.
 *
 * @param key The PreferenceKey to render
 * @param preferences The Preferences instance
 * @param config The Config instance
 * @param profileUtil Required for UnitDoublePreferenceKey
 * @param onIntentClick Optional click handler for IntentPreferenceKey with CLICK type
 * @param intentUrl Optional URL for IntentPreferenceKey with URL type
 * @param intentActivityClass Optional Activity class for IntentPreferenceKey with ACTIVITY type
 */
@Composable
fun AdaptivePreferenceItem(
    key: PreferenceKey,
    preferences: Preferences,
    config: Config,
    profileUtil: ProfileUtil? = null,
    onIntentClick: (() -> Unit)? = null,
    intentUrl: String? = null,
    intentActivityClass: Class<*>? = null
) {
    when (key) {
        is BooleanPreferenceKey -> {
            AdaptiveSwitchPreferenceItem(
                preferences = preferences,
                config = config,
                booleanKey = key
            )
        }

        is IntPreferenceKey      -> {
            when (key.preferenceType) {
                PreferenceType.LIST       -> {
                    if (key.entries.isNotEmpty()) {
                        val entryValues = key.entries.keys.toList()
                        val entries = key.entries.values.map { stringResource(it) }
                        AdaptiveListIntPreferenceItem(
                            preferences = preferences,
                            config = config,
                            intKey = key,
                            entries = entries,
                            entryValues = entryValues
                        )
                    }
                }

                PreferenceType.TEXT_FIELD -> {
                    AdaptiveIntPreferenceItem(
                        preferences = preferences,
                        config = config,
                        intKey = key
                    )
                }

                else                      -> {
                    // Default to text field for unsupported types
                    AdaptiveIntPreferenceItem(
                        preferences = preferences,
                        config = config,
                        intKey = key
                    )
                }
            }
        }

        is DoublePreferenceKey   -> {
            AdaptiveDoublePreferenceItem(
                preferences = preferences,
                config = config,
                doubleKey = key
            )
        }

        is StringPreferenceKey   -> {
            when (key.preferenceType) {
                PreferenceType.LIST       -> {
                    if (key.entries.isNotEmpty()) {
                        // Convert Map<String, Int> (value -> labelResId) to Map<String, String> (value -> label)
                        val entriesMap = key.entries.mapValues { (_, resId) -> stringResource(resId) }
                        AdaptiveStringListPreferenceItem(
                            preferences = preferences,
                            config = config,
                            stringKey = key,
                            entries = entriesMap
                        )
                    }
                }

                PreferenceType.TEXT_FIELD -> {
                    AdaptiveStringPreferenceItem(
                        preferences = preferences,
                        config = config,
                        stringKey = key
                    )
                }

                else                      -> {
                    AdaptiveStringPreferenceItem(
                        preferences = preferences,
                        config = config,
                        stringKey = key
                    )
                }
            }
        }

        is UnitDoublePreferenceKey -> {
            profileUtil?.let {
                AdaptiveUnitDoublePreferenceItem(
                    preferences = preferences,
                    config = config,
                    profileUtil = it,
                    unitKey = key
                )
            }
        }

        is IntentPreferenceKey   -> {
            when (key.preferenceType) {
                PreferenceType.URL      -> {
                    intentUrl?.let { url ->
                        AdaptiveUrlPreferenceItem(
                            preferences = preferences,
                            intentKey = key,
                            url = url
                        )
                    }
                }

                PreferenceType.ACTIVITY -> {
                    intentActivityClass?.let { activityClass ->
                        AdaptiveDynamicActivityPreferenceItem(
                            preferences = preferences,
                            intentKey = key,
                            activityClass = activityClass
                        )
                    }
                }

                PreferenceType.CLICK    -> {
                    onIntentClick?.let { onClick ->
                        AdaptiveIntentPreferenceItem(
                            preferences = preferences,
                            intentKey = key,
                            onClick = onClick
                        )
                    }
                }

                else                    -> {
                    onIntentClick?.let { onClick ->
                        AdaptiveIntentPreferenceItem(
                            preferences = preferences,
                            intentKey = key,
                            onClick = onClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * LazyListScope extension for rendering a preference based on its PreferenceKey.
 */
fun LazyListScope.adaptivePreference(
    key: PreferenceKey,
    preferences: Preferences,
    config: Config,
    profileUtil: ProfileUtil? = null,
    onIntentClick: (() -> Unit)? = null,
    intentUrl: String? = null,
    intentActivityClass: Class<*>? = null,
    keyPrefix: String = ""
) {
    val itemKey = if (keyPrefix.isNotEmpty()) "${keyPrefix}_${key.key}" else key.key

    item(key = itemKey, contentType = "AdaptivePreference_${key::class.simpleName}") {
        AdaptivePreferenceItem(
            key = key,
            preferences = preferences,
            config = config,
            profileUtil = profileUtil,
            onIntentClick = onIntentClick,
            intentUrl = intentUrl,
            intentActivityClass = intentActivityClass
        )
    }
}

/**
 * Renders a list of preferences from PreferenceKeys.
 * This is the main entry point for auto-generating preference screens.
 *
 * @param keys List of PreferenceKeys to render
 * @param preferences The Preferences instance
 * @param config The Config instance
 * @param profileUtil Required for UnitDoublePreferenceKey
 * @param intentHandlers Map of IntentPreferenceKey to handler info (optional - for dynamic values)
 * @param visibilityContext Optional context for evaluating runtime visibility conditions
 */
@Composable
fun AdaptivePreferenceList(
    keys: List<PreferenceKey>,
    preferences: Preferences,
    config: Config,
    profileUtil: ProfileUtil? = null,
    intentHandlers: Map<IntentPreferenceKey, IntentHandler> = emptyMap(),
    visibilityContext: PreferenceVisibilityContext? = null
) {
    // Filter keys by runtime visibility if context is provided
    val visibleKeys = if (visibilityContext != null) {
        keys.filter { key -> key.visibility.isVisible(visibilityContext) }
    } else {
        keys
    }

    visibleKeys.forEach { key ->
        if (key is IntentPreferenceKey) {
            // Priority: 1) intentHandlers map, 2) key properties
            val handler = intentHandlers[key]
            val resolvedUrl = handler?.url
                ?: key.urlResId?.let { stringResource(it) }
            val resolvedActivityClass = handler?.activityClass
                ?: key.activityClass
            val resolvedOnClick = handler?.onClick

            AdaptivePreferenceItem(
                key = key,
                preferences = preferences,
                config = config,
                profileUtil = profileUtil,
                onIntentClick = resolvedOnClick,
                intentUrl = resolvedUrl,
                intentActivityClass = resolvedActivityClass
            )
        } else {
            AdaptivePreferenceItem(
                key = key,
                preferences = preferences,
                config = config,
                profileUtil = profileUtil
            )
        }
    }
}

/**
 * Handler info for IntentPreferenceKey.
 * Provide one of: onClick, url, or activityClass based on preferenceType.
 */
data class IntentHandler(
    val onClick: (() -> Unit)? = null,
    val url: String? = null,
    val activityClass: Class<*>? = null
)
