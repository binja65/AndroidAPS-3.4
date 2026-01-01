package app.aaps.compose.preferences

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.main.skins.SkinInterface

/**
 * Compose implementation of General preferences.
 * Most preferences are key-based. Only Skin requires dynamic entries from plugins.
 */
class GeneralPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val skins: List<SkinInterface>,
    private val getSkinDescription: (SkinInterface) -> String
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.plugins.configuration.R.string.configbuilder_general

    override val mainKeys: List<PreferenceKey> = listOf(
        StringKey.GeneralUnits,
        StringKey.GeneralLanguage,
        BooleanKey.GeneralSimpleMode,
        StringKey.GeneralPatientName,
        StringKey.GeneralSkin,
        StringKey.GeneralDarkMode
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Units, Language - key-based with entries
        AdaptivePreferenceList(
            keys = listOf(StringKey.GeneralUnits, StringKey.GeneralLanguage),
            preferences = preferences,
            config = config
        )

        // Simple mode and patient name - key-based
        AdaptivePreferenceList(
            keys = listOf(BooleanKey.GeneralSimpleMode, StringKey.GeneralPatientName),
            preferences = preferences,
            config = config
        )

        // Skin - requires dynamic entries from plugins (cannot be key-based)
        if (skins.isNotEmpty()) {
            val skinEntries = skins.associate { skin ->
                skin.javaClass.name to getSkinDescription(skin)
            }
            AdaptiveStringListPreferenceItem(
                preferences = preferences,
                config = config,
                stringKey = StringKey.GeneralSkin,
                titleResId = app.aaps.plugins.main.R.string.skin,
                entries = skinEntries
            )
        }

        // Dark Mode - key-based with entries
        AdaptivePreferenceList(
            keys = listOf(StringKey.GeneralDarkMode),
            preferences = preferences,
            config = config
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
