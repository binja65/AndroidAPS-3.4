package app.aaps.compose.preferences

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.aaps.R
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.StringKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.UiMode
import app.aaps.core.ui.compose.preference.AdaptiveStringListPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveStringPreferenceItem
import app.aaps.core.ui.compose.preference.AdaptiveSwitchPreferenceItem
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.plugins.main.skins.SkinInterface

/**
 * Compose implementation of General preferences.
 */
class GeneralPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val skins: List<SkinInterface>,
    private val getSkinDescription: (SkinInterface) -> String
) : NavigablePreferenceContent {

    override val titleResId: Int = app.aaps.plugins.configuration.R.string.configbuilder_general

    override val summaryItems: List<Int> = listOf(
        R.string.unitsnosemicolon,
        R.string.language,
        R.string.simple_mode,
        app.aaps.plugins.main.R.string.skin
    )

    override val mainContent: (@Composable (PreferenceSectionState?) -> Unit) = { _ ->
        // Units
        val unitsEntries = mapOf(
            "mg/dl" to "mg/dL",
            "mmol" to "mmol/L"
        )
        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.GeneralUnits,
            titleResId = R.string.unitsnosemicolon,
            entries = unitsEntries
        )

        // Language
        val defaultLang = stringResource(R.string.default_lang)
        val languageEntries = linkedMapOf(
            "default" to defaultLang,
            "en" to "English",
            "af" to "Afrikaans",
            "bg" to "Bulgarian",
            "cs" to "Czech",
            "de" to "German",
            "dk" to "Danish",
            "fr" to "French",
            "nl" to "Dutch",
            "es" to "Spanish",
            "el" to "Greek",
            "ga" to "Irish",
            "it" to "Italian",
            "ko" to "Korean",
            "lt" to "Lithuanian",
            "nb" to "Norwegian",
            "pl" to "Polish",
            "pt" to "Portuguese",
            "pt_BR" to "Portuguese, Brazilian",
            "ro" to "Romanian",
            "ru" to "Russian",
            "sk" to "Slovak",
            "sv" to "Swedish",
            "tr" to "Turkish",
            "zh_TW" to "Chinese, Traditional",
            "zh_CN" to "Chinese, Simplified"
        )
        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.GeneralLanguage,
            titleResId = R.string.language,
            entries = languageEntries
        )

        // Simple Mode
        AdaptiveSwitchPreferenceItem(
            preferences = preferences,
            config = config,
            booleanKey = BooleanKey.GeneralSimpleMode,
            titleResId = R.string.simple_mode
        )

        // Patient Name
        AdaptiveStringPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.GeneralPatientName,
            titleResId = app.aaps.plugins.configuration.R.string.patient_name,
            summaryResId = app.aaps.plugins.configuration.R.string.patient_name_summary
        )

        // Skin
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

        // Dark Mode
        val darkModeEntries = mapOf(
            UiMode.DARK.stringValue to stringResource(app.aaps.plugins.main.R.string.dark_theme),
            UiMode.LIGHT.stringValue to stringResource(app.aaps.plugins.main.R.string.light_theme),
            UiMode.SYSTEM.stringValue to stringResource(app.aaps.plugins.main.R.string.follow_system_theme)
        )
        AdaptiveStringListPreferenceItem(
            preferences = preferences,
            config = config,
            stringKey = StringKey.GeneralDarkMode,
            titleResId = app.aaps.plugins.main.R.string.app_color_scheme,
            entries = darkModeEntries
        )
    }

    override val subscreens: List<PreferenceSubScreen> = emptyList()
}
