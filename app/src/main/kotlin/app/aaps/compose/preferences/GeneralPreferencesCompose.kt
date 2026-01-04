package app.aaps.compose.preferences

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.keys.interfaces.withEntries
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.main.skins.SkinInterface

/**
 * Compose implementation of General preferences.
 * Most preferences are key-based. Skin uses withEntries for dynamic plugin entries.
 */
class GeneralPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val rh: ResourceHelper,
    skins: List<SkinInterface>
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.plugins.configuration.R.string.configbuilder_general

    override val mainKeys: List<PreferenceKey> = listOf(
        StringKey.GeneralUnits,
        StringKey.GeneralLanguage,
        BooleanKey.GeneralSimpleMode,
        StringKey.GeneralPatientName,
        StringKey.GeneralSkin.withEntries(skins.associate { skin -> skin.javaClass.name to rh.gs(skin.description) }),
        StringKey.GeneralDarkMode
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        AdaptivePreferenceList(
            keys = mainKeys,
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
