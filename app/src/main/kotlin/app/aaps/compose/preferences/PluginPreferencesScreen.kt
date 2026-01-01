package app.aaps.compose.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.aaps.R
import app.aaps.core.interfaces.plugin.PluginBase
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceNavigationHost
import app.aaps.core.ui.compose.preference.ProvidePreferenceTheme
import app.aaps.core.ui.compose.preference.addNavigablePreferenceContent
import app.aaps.core.ui.compose.preference.rememberPreferenceSectionState
import app.aaps.core.ui.compose.preference.verticalScrollIndicators

/**
 * Screen for displaying plugin preferences using Compose.
 * Uses NavigablePreferenceContent for hierarchical navigation.
 *
 * @param plugin The plugin whose preferences to display
 * @param onBackClick Callback when back button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginPreferencesScreen(
    plugin: PluginBase,
    onBackClick: () -> Unit
) {
    val preferenceScreenContent = plugin.getPreferenceScreenContent()
    val title = stringResource(R.string.nav_preferences_plugin, plugin.name)

    ProvidePreferenceTheme {
        when (preferenceScreenContent) {
            is NavigablePreferenceContent -> {
                // Use navigation-based subscreens (click to open)
                PreferenceNavigationHost(
                    content = preferenceScreenContent,
                    title = title,
                    onBackClick = onBackClick
                )
            }

            else                          -> {
                // Fallback for plugins without compose preferences
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Text(
                            text = "No compose preferences available for this plugin",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Screen for displaying all preferences from all plugins.
 *
 * @param plugins List of plugins to display preferences for
 * @param onBackClick Callback when back button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllPreferencesScreen(
    plugins: List<PluginBase>,
    onBackClick: () -> Unit
) {
    val navigableContents = plugins
        .mapNotNull { it.getPreferenceScreenContent() }
        .filterIsInstance<NavigablePreferenceContent>()
        .distinctBy { it.keyPrefix }

    ProvidePreferenceTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(app.aaps.core.ui.R.string.settings),
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
            val sectionState = rememberPreferenceSectionState()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScrollIndicators(listState),
                state = listState
            ) {
                navigableContents.forEach { content ->
                    addNavigablePreferenceContent(content, sectionState)
                }
            }
        }
    }
}
