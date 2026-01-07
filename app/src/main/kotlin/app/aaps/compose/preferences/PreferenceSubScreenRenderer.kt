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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.plugin.PluginBase
import app.aaps.core.interfaces.plugin.PluginBaseWithPreferences
import app.aaps.core.interfaces.profile.ProfileUtil
import app.aaps.core.keys.interfaces.PreferenceVisibilityContext
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.PreferenceSubScreenDef
import app.aaps.core.ui.compose.preference.verticalScrollIndicators

/**
 * Generic renderer for PreferenceSubScreenDef structure.
 * Handles navigation between main screen and nested subscreens.
 *
 * @param screen The root preference screen structure
 * @param title Title for the main screen
 * @param plugin The plugin (used to extract preferences and config)
 * @param onBackClick Callback when back button is clicked on main screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceSubScreenRenderer(
    screen: PreferenceSubScreenDef,
    title: String,
    plugin: PluginBase,
    config: Config,
    profileUtil: ProfileUtil,
    visibilityContext: PreferenceVisibilityContext? = null,
    onBackClick: () -> Unit
) {
    // Extract dependencies from plugin
    val preferences = (plugin as? PluginBaseWithPreferences)?.preferences
        ?: return Text("Plugin does not support preferences")

    var navigationStack by rememberSaveable { mutableStateOf(listOf(screen)) }
    val currentScreen = navigationStack.last()
    val isMainScreen = navigationStack.size == 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isMainScreen) title else stringResource(currentScreen.titleResId),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isMainScreen) {
                            onBackClick()
                        } else {
                            navigationStack = navigationStack.dropLast(1)
                        }
                    }) {
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
            item(key = "screen_content") {
                Column {
                    // Use customContent if provided, otherwise auto-render with AdaptivePreferenceList
                    if (currentScreen.customContent != null) {
                        currentScreen.customContent!!.invoke(null)
                    } else {
                        // Auto-render using enhanced AdaptivePreferenceList
                        AdaptivePreferenceList(
                            items = currentScreen.effectiveItems,
                            preferences = preferences,
                            config = config,
                            profileUtil = profileUtil,
                            visibilityContext = visibilityContext,
                            onNavigateToSubScreen = { subscreen ->
                                navigationStack = navigationStack + subscreen
                            }
                        )
                    }
                }
            }
        }
    }
}
