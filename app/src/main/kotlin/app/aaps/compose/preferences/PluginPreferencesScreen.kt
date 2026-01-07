package app.aaps.compose.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.plugin.PluginBase
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.ui.compose.preference.PreferenceSubScreenDef
import app.aaps.core.ui.compose.preference.ProvidePreferenceTheme
import app.aaps.core.ui.compose.preference.navigable.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.navigable.PreferenceNavigationHost

/**
 * Screen for displaying plugin preferences using Compose.
 * Uses NavigablePreferenceContent for hierarchical navigation.
 *
 * @param plugin The plugin whose preferences to display
 * @param config Config instance for rendering
 * @param profileUtil ProfileUtil instance for unit preferences
 * @param visibilityContext Context for evaluating visibility conditions
 * @param onBackClick Callback when back button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginPreferencesScreen(
    plugin: PluginBase,
    config: Config,
    profileUtil: ProfileUtil,
    visibilityContext: PreferenceVisibilityContext? = null,
    onBackClick: () -> Unit
) {
    val preferenceScreenContent = plugin.getPreferenceScreenContent()
    val title = stringResource(R.string.nav_preferences_plugin, plugin.name)

    ProvidePreferenceTheme {
        when (preferenceScreenContent) {
            is PreferenceSubScreenDef -> {
                // New pattern: PreferenceSubScreenDef with structure defined in plugin
                PreferenceSubScreenRenderer(
                    screen = preferenceScreenContent,
                    title = title,
                    plugin = plugin,
                    config = config,
                    profileUtil = profileUtil,
                    visibilityContext = visibilityContext,
                    onBackClick = onBackClick
                )
            }

            is NavigablePreferenceContent -> {
                // Legacy pattern: NavigablePreferenceContent with separate compose class
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
