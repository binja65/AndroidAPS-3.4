package app.aaps.core.ui.compose.preference.navigable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.aaps.core.ui.compose.preference.LocalPreferenceTheme

/**
 * A clickable preference item that navigates to a subscreen.
 * Shows a title, optional summary, and a navigation arrow.
 *
 * @param titleResId String resource ID for the title
 * @param summaryResId Optional string resource ID for summary text
 * @param summaryItems Optional list of preference title resource IDs to show as summary
 * @param onClick Callback when the item is clicked
 * @deprecated Legacy pattern - used with NavigablePreferenceContent
 */
@Composable
fun NavigablePreferenceItem(
    titleResId: Int,
    summaryResId: Int? = null,
    summaryItems: List<Int> = emptyList(),
    onClick: () -> Unit
) {
    val theme = LocalPreferenceTheme.current

    // Build summary text from summaryItems if provided
    // Need to resolve strings before joinToString since stringResource is @Composable
    val resolvedSummaryItems = summaryItems.map { stringResource(it) }
    val summaryText = when {
        resolvedSummaryItems.isNotEmpty() -> resolvedSummaryItems.joinToString(", ")
        summaryResId != null              -> stringResource(summaryResId)
        else                              -> null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            ProvideTextStyle(value = theme.titleTextStyle) {
                Text(
                    text = stringResource(titleResId),
                    color = theme.titleColor
                )
            }
            if (summaryText != null) {
                ProvideTextStyle(value = theme.summaryTextStyle) {
                    Text(
                        text = summaryText,
                        color = theme.summaryColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
