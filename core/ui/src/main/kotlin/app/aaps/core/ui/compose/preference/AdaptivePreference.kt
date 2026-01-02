/*
 * Adaptive Preference Support for Jetpack Compose
 *
 * This file serves as a barrel export for backward compatibility.
 * All preference components have been extracted to separate files:
 *
 * - PreferenceState.kt: State classes and visibility calculation
 * - AdaptiveSwitchPreference.kt: Switch/Boolean preferences
 * - AdaptiveIntPreference.kt: Int preferences
 * - AdaptiveDoublePreference.kt: Double preferences
 * - AdaptiveStringPreference.kt: String preferences
 * - AdaptiveListPreference.kt: List preferences (int and string)
 * - AdaptiveIntentPreference.kt: Intent/URL/Activity preferences
 * - AdaptiveUnitDoublePreference.kt: Unit double preferences (glucose units)
 * - AdaptivePreferenceRenderer.kt: Generic rendering and AdaptivePreferenceList
 */

@file:Suppress("unused")

package app.aaps.core.ui.compose.preference

// Re-export all public APIs for backward compatibility

// From PreferenceState.kt
// - PreferenceVisibilityState (data class)
// - calculatePreferenceVisibility
// - calculateIntentPreferenceVisibility
// - rememberPreferenceBooleanState
// - rememberPreferenceStringState
// - rememberPreferenceIntState
// - rememberPreferenceDoubleState
// - UnitDoublePreferenceState
// - rememberUnitDoublePreferenceState

// From AdaptiveSwitchPreference.kt
// - adaptiveSwitchPreference (LazyListScope)
// - AdaptiveSwitchPreferenceItem (Composable)

// From AdaptiveIntPreference.kt
// - adaptiveIntPreference (LazyListScope)
// - AdaptiveIntPreferenceItem (Composable)

// From AdaptiveDoublePreference.kt
// - adaptiveDoublePreference (LazyListScope)
// - AdaptiveDoublePreferenceItem (Composable)

// From AdaptiveStringPreference.kt
// - adaptiveStringPreference (LazyListScope)
// - AdaptiveStringPreferenceItem (Composable)

// From AdaptiveListPreference.kt
// - adaptiveListIntPreference (LazyListScope)
// - adaptiveStringListPreference (LazyListScope)
// - AdaptiveListIntPreferenceItem (Composable)
// - AdaptiveStringListPreferenceItem (Composable)

// From AdaptiveIntentPreference.kt
// - adaptiveIntentPreference (LazyListScope)
// - adaptiveUrlPreference (LazyListScope)
// - adaptiveActivityPreference (LazyListScope)
// - adaptiveDynamicActivityPreference (LazyListScope)
// - AdaptiveIntentPreferenceItem (Composable)
// - AdaptiveUrlPreferenceItem (Composable)
// - AdaptiveActivityPreferenceItem (Composable)
// - AdaptiveDynamicActivityPreferenceItem (Composable)

// From AdaptiveUnitDoublePreference.kt
// - AdaptiveUnitDoublePreferenceItem (Composable)

// From AdaptivePreferenceRenderer.kt
// - AdaptivePreferenceItem (Composable)
// - adaptivePreference (LazyListScope)
// - AdaptivePreferenceList (Composable)
// - IntentHandler (data class)
