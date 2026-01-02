package app.aaps.plugins.main.general.overview.keys

import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.PreferenceType
import app.aaps.core.keys.interfaces.BooleanPreferenceKey
import app.aaps.core.keys.interfaces.IntPreferenceKey
import app.aaps.core.keys.interfaces.PreferenceVisibility
import app.aaps.plugins.main.R

enum class OverviewIntKey(
    override val key: String,
    override val defaultValue: Int,
    override val min: Int,
    override val max: Int,
    override val titleResId: Int = 0,
    override val summaryResId: Int? = null,
    override val preferenceType: PreferenceType = PreferenceType.TEXT_FIELD,
    override val entries: Map<Int, Int> = emptyMap(),
    override val defaultedBySM: Boolean = false,
    override val calculatedDefaultValue: Boolean = false,
    override val showInApsMode: Boolean = true,
    override val showInNsClientMode: Boolean = true,
    override val showInPumpControlMode: Boolean = true,
    override val dependency: BooleanPreferenceKey? = null,
    override val negativeDependency: BooleanPreferenceKey? = null,
    override val hideParentScreenIfHidden: Boolean = false,
    override val engineeringModeOnly: Boolean = false,
    override val exportable: Boolean = true,
    override val visibility: PreferenceVisibility = PreferenceVisibility.ALWAYS
) : IntPreferenceKey {

    IageWarning(key = "statuslights_iage_warning", defaultValue = 72, min = 24, max = 240, titleResId = R.string.statuslights_iage_warning, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights, visibility = PreferenceVisibility.NON_PATCH_PUMP),
    IageCritical(key = "statuslights_iage_critical", defaultValue = 144, min = 24, max = 240, titleResId = R.string.statuslights_iage_critical, defaultedBySM = true, dependency = BooleanKey.OverviewShowStatusLights, visibility = PreferenceVisibility.NON_PATCH_PUMP),
}
