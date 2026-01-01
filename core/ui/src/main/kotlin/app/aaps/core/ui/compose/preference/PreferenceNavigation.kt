package app.aaps.core.ui.compose.preference

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.aaps.core.keys.interfaces.PreferenceKey

/**
 * Represents a preference subscreen that can be navigated to.
 *
 * @param key Unique key for this subscreen
 * @param titleResId String resource ID for the screen title
 * @param keys List of preference keys - summaryItems is auto-derived from keys' titleResId (preferred)
 * @param summaryItems Direct list of string resource IDs for summary (legacy, use keys instead)
 * @param summaryResId Optional string resource ID for summary shown in parent list
 * @param content Composable lambda that provides the preference content for this subscreen
 */
class PreferenceSubScreen(
    val key: String,
    val titleResId: Int,
    val keys: List<PreferenceKey> = emptyList(),
    val summaryItems: List<Int> = emptyList(),
    val summaryResId: Int? = null,
    val content: @Composable (PreferenceSectionState?) -> Unit
) {
    /** Effective summary items - from keys if provided, otherwise direct summaryItems */
    fun effectiveSummaryItems(): List<Int> =
        if (keys.isNotEmpty()) keys.map { it.titleResId }.filter { it != 0 }
        else summaryItems
}

/**
 * Interface for plugins that provide navigable preference subscreens.
 * Plugins implement this to define their preference UI with hierarchical navigation.
 */
interface NavigablePreferenceContent {

    /**
     * Unique key prefix for this plugin's preferences.
     */
    val keyPrefix: String
        get() = this::class.java.simpleName

    /**
     * String resource ID for the main section title.
     */
    val titleResId: Int

    /**
     * Main preference keys shown at top level.
     * Summary is auto-derived from keys' titleResId.
     */
    val mainKeys: List<PreferenceKey>
        get() = emptyList()

    /**
     * Optional list of preference title resource IDs to show as summary when collapsed.
     * Used when the card is collapsed to show what preferences are available.
     * If empty, derived from mainKeys + subscreens keys.
     */
    val summaryItems: List<Int>
        get() = emptyList()

    /**
     * Effective summary items - from mainKeys + subscreens if available, otherwise summaryItems.
     */
    fun effectiveSummaryItems(): List<Int> {
        val fromKeys = mainKeys.map { it.titleResId }.filter { it != 0 } +
            subscreens.flatMap { it.keys.map { k -> k.titleResId } }.filter { it != 0 }
        return if (fromKeys.isNotEmpty()) fromKeys else summaryItems.ifEmpty {
            subscreens.map { it.titleResId }
        }
    }

    /**
     * Optional main content shown at top level (not in a subscreen).
     * These preferences are displayed directly, with subscreens below.
     */
    val mainContent: (@Composable (PreferenceSectionState?) -> Unit)?
        get() = null

    /**
     * List of subscreens available in this preference screen.
     */
    val subscreens: List<PreferenceSubScreen>
}

/**
 * Composable that hosts preference navigation with subscreens.
 * Displays a list of subscreens and navigates to them when clicked.
 *
 * @param content The navigable preference content
 * @param title Title for the main screen
 * @param onBackClick Callback when back button is clicked on main screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceNavigationHost(
    content: NavigablePreferenceContent,
    title: String,
    onBackClick: () -> Unit
) {
    var currentSubScreen by rememberSaveable { mutableStateOf<String?>(null) }
    val sectionState = rememberPreferenceSectionState()

    val selectedSubScreen = content.subscreens.find { it.key == currentSubScreen }

    AnimatedContent(
        targetState = selectedSubScreen,
        transitionSpec = {
            if (targetState != null) {
                // Navigating to subscreen - slide in from right
                slideInHorizontally { width -> width } togetherWith
                    slideOutHorizontally { width -> -width }
            } else {
                // Navigating back - slide in from left
                slideInHorizontally { width -> -width } togetherWith
                    slideOutHorizontally { width -> width }
            }
        },
        label = "PreferenceNavigation"
    ) { subScreen ->
        if (subScreen != null) {
            // Show subscreen
            PreferenceSubScreenScaffold(
                titleResId = subScreen.titleResId,
                onBackClick = { currentSubScreen = null }
            ) {
                val listState = rememberLazyListState()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScrollIndicators(listState),
                    state = listState
                ) {
                    item {
                        subScreen.content(sectionState)
                    }
                }
            }
        } else {
            // Show main screen with main content + subscreen list
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(app.aaps.core.ui.R.string.back)
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                val listState = rememberLazyListState()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScrollIndicators(listState),
                    state = listState
                ) {
                    // Show main content first (if any)
                    content.mainContent?.let { mainContent ->
                        item(key = "${content.keyPrefix}_main") {
                            mainContent(sectionState)
                        }
                    }
                    // Then show subscreen navigation items
                    content.subscreens.forEach { subScreen ->
                        item(key = subScreen.key) {
                            NavigablePreferenceItem(
                                titleResId = subScreen.titleResId,
                                summaryResId = subScreen.summaryResId,
                                summaryItems = subScreen.effectiveSummaryItems(),
                                onClick = { currentSubScreen = subScreen.key }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Scaffold for a preference subscreen with back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreferenceSubScreenScaffold(
    titleResId: Int,
    onBackClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(titleResId),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(app.aaps.core.ui.R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}

/**
 * A clickable preference item that navigates to a subscreen.
 * Shows a title, optional summary, and a navigation arrow.
 *
 * @param titleResId String resource ID for the title
 * @param summaryResId Optional string resource ID for summary text
 * @param summaryItems Optional list of preference title resource IDs to show as summary
 * @param onClick Callback when the item is clicked
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

/**
 * Helper function to add NavigablePreferenceContent inline in a LazyListScope.
 * This displays as one collapsible card with main content and subscreen items inside.
 */
fun LazyListScope.addNavigablePreferenceContent(
    content: NavigablePreferenceContent,
    sectionState: PreferenceSectionState? = null
) {
    val sectionKey = "${content.keyPrefix}_main"
    item(key = sectionKey) {
        val isExpanded = sectionState?.isExpanded(sectionKey) ?: false
        CollapsibleCardSectionContent(
            titleResId = content.titleResId,
            summaryItems = content.effectiveSummaryItems(),
            expanded = isExpanded,
            onToggle = { sectionState?.toggle(sectionKey) }
        ) {
            // Show main content first (if any)
            content.mainContent?.invoke(sectionState)
            // Then show each subscreen as a collapsible section
            content.subscreens.forEach { subScreen ->
                val subSectionKey = "${content.keyPrefix}_${subScreen.key}"
                val isSubExpanded = sectionState?.isExpanded(subSectionKey) ?: false
                CollapsibleCardSectionContent(
                    titleResId = subScreen.titleResId,
                    summaryItems = subScreen.effectiveSummaryItems(),
                    expanded = isSubExpanded,
                    onToggle = { sectionState?.toggle(subSectionKey) }
                ) {
                    subScreen.content(sectionState)
                }
            }
        }
    }
}

/**
 * Host composable for rendering subscreen content inside a CollapsibleCardSectionContent.
 */
@Composable
private fun SubScreenContentHost(
    subScreen: PreferenceSubScreen,
    sectionState: PreferenceSectionState?
) {
    // Render the subscreen's composable content directly
    subScreen.content(sectionState)
}

/**
 * A navigable preference item for use inside CollapsibleCardSectionContent.
 * Similar to NavigablePreferenceItem but styled for card context.
 */
@Composable
fun NavigableCardPreferenceItem(
    titleResId: Int,
    summaryResId: Int? = null,
    onClick: () -> Unit
) {
    val theme = LocalPreferenceTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
            if (summaryResId != null) {
                ProvideTextStyle(value = theme.summaryTextStyle) {
                    Text(
                        text = stringResource(summaryResId),
                        color = theme.summaryColor,
                        maxLines = 1,
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
