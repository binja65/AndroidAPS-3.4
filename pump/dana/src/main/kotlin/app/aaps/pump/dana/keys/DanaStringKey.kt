package app.aaps.pump.dana.keys

import app.aaps.core.keys.interfaces.BooleanPreferenceKey
import app.aaps.core.keys.interfaces.StringPreferenceKey
import app.aaps.pump.dana.R

enum class DanaStringKey(
    override val key: String,
    override val defaultValue: String,
    override val titleResId: Int = 0,
    override val defaultedBySM: Boolean = false,
    override val showInApsMode: Boolean = true,
    override val showInNsClientMode: Boolean = true,
    override val showInPumpControlMode: Boolean = true,
    override val dependency: BooleanPreferenceKey? = null,
    override val negativeDependency: BooleanPreferenceKey? = null,
    override val hideParentScreenIfHidden: Boolean = false,
    override val isPassword: Boolean = false,
    override val isPin: Boolean = false,
    override val exportable: Boolean = true
) : StringPreferenceKey {

    RName("danar_bt_name", "", titleResId = R.string.danar_bt_name_title),
    RsName("danars_name", "", titleResId = R.string.selectedpump),
    MacAddress("danars_address", ""),
    Password("danars_password", "", titleResId = R.string.danars_password_title, isPassword = true),
}
