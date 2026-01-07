package app.aaps.core.ui.compose.preference

import androidx.compose.runtime.Composable
import app.aaps.core.keys.interfaces.PreferenceItem

/**
 * A custom composable preference that can render arbitrary Compose content.
 * Use this for preferences that don't fit standard patterns.
 *
 * @param key Unique identifier for this preference item
 * @param content Composable content to render
 */
class ComposablePreferenceItem(
    val key: String,
    val content: @Composable () -> Unit
) : PreferenceItem
