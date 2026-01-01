package app.aaps.core.keys

import app.aaps.core.keys.interfaces.BooleanPreferenceKey
import app.aaps.core.keys.interfaces.StringPreferenceKey

enum class StringKey(
    override val key: String,
    override val defaultValue: String,
    override val titleResId: Int = 0,
    override val summaryResId: Int? = null,
    override val preferenceType: PreferenceType = PreferenceType.TEXT_FIELD,
    override val entries: Map<String, Int> = emptyMap(),
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

    GeneralUnits(
        key = "units",
        defaultValue = "mg/dl",
        titleResId = R.string.pref_title_units,
        preferenceType = PreferenceType.LIST,
        entries = mapOf(
            "mg/dl" to R.string.units_mgdl,
            "mmol" to R.string.units_mmol
        )
    ),
    GeneralLanguage(
        key = "language",
        defaultValue = "default",
        titleResId = R.string.pref_title_language,
        preferenceType = PreferenceType.LIST,
        entries = mapOf(
            "default" to R.string.lang_default,
            "en" to R.string.lang_en,
            "af" to R.string.lang_af,
            "bg" to R.string.lang_bg,
            "cs" to R.string.lang_cs,
            "de" to R.string.lang_de,
            "dk" to R.string.lang_dk,
            "fr" to R.string.lang_fr,
            "nl" to R.string.lang_nl,
            "es" to R.string.lang_es,
            "el" to R.string.lang_el,
            "ga" to R.string.lang_ga,
            "it" to R.string.lang_it,
            "ko" to R.string.lang_ko,
            "lt" to R.string.lang_lt,
            "nb" to R.string.lang_nb,
            "pl" to R.string.lang_pl,
            "pt" to R.string.lang_pt,
            "pt_BR" to R.string.lang_pt_br,
            "ro" to R.string.lang_ro,
            "ru" to R.string.lang_ru,
            "sk" to R.string.lang_sk,
            "sv" to R.string.lang_sv,
            "tr" to R.string.lang_tr,
            "zh_TW" to R.string.lang_zh_tw,
            "zh_CN" to R.string.lang_zh_cn
        ),
        defaultedBySM = true
    ),
    GeneralPatientName(key = "patient_name", defaultValue = "", titleResId = R.string.pref_title_patient_name),
    GeneralSkin(key = "skin", defaultValue = "", titleResId = R.string.pref_title_skin),
    GeneralDarkMode(
        key = "use_dark_mode",
        defaultValue = "dark",
        titleResId = R.string.pref_title_dark_mode,
        preferenceType = PreferenceType.LIST,
        entries = mapOf(
            "dark" to R.string.dark_mode_dark,
            "light" to R.string.dark_mode_light,
            "system" to R.string.dark_mode_system
        ),
        defaultedBySM = true
    ),

    AapsDirectoryUri(key = "aaps_directory", defaultValue = "", titleResId = R.string.pref_title_aaps_directory),

    ProtectionMasterPassword(key = "master_password", defaultValue = "", titleResId = R.string.pref_title_master_password, isPassword = true),
    ProtectionSettingsPassword(key = "settings_password", defaultValue = "", titleResId = R.string.pref_title_settings_password, isPassword = true),
    ProtectionSettingsPin(key = "settings_pin", defaultValue = "", titleResId = R.string.pref_title_settings_pin, isPin = true),
    ProtectionApplicationPassword(key = "application_password", defaultValue = "", titleResId = R.string.pref_title_application_password, isPassword = true),
    ProtectionApplicationPin(key = "application_pin", defaultValue = "", titleResId = R.string.pref_title_application_pin, isPin = true),
    ProtectionBolusPassword(key = "bolus_password", defaultValue = "", titleResId = R.string.pref_title_bolus_password, isPassword = true),
    ProtectionBolusPin(key = "bolus_pin", defaultValue = "", titleResId = R.string.pref_title_bolus_pin, isPin = true),

    OverviewCopySettingsFromNs(key = "statuslights_copy_ns", defaultValue = "", titleResId = R.string.pref_title_copy_settings_from_ns, dependency = BooleanKey.OverviewShowStatusLights),

    SafetyAge(key = "age", defaultValue = "adult", titleResId = R.string.pref_title_patient_age),
    MaintenanceEmail(key = "maintenance_logs_email", defaultValue = "logs@aaps.app", titleResId = R.string.pref_title_logs_email, defaultedBySM = true),
    MaintenanceIdentification(key = "email_for_crash_report", defaultValue = "", titleResId = R.string.pref_title_identification),
    AutomationLocation(
        key = "location",
        defaultValue = "PASSIVE",
        titleResId = R.string.pref_title_automation_location,
        preferenceType = PreferenceType.LIST,
        entries = mapOf(
            "PASSIVE" to R.string.automation_location_passive,
            "NETWORK" to R.string.automation_location_network,
            "GPS" to R.string.automation_location_gps
        ),
        hideParentScreenIfHidden = true
    ),

    SmsAllowedNumbers(key = "smscommunicator_allowednumbers", defaultValue = "", titleResId = R.string.pref_title_sms_allowed_numbers),
    SmsOtpPassword(key = "smscommunicator_otp_password", defaultValue = "", titleResId = R.string.pref_title_sms_otp_password, dependency = BooleanKey.SmsAllowRemoteCommands, isPassword = true),

    VirtualPumpType(key = "virtualpump_type", defaultValue = "Generic AAPS", titleResId = R.string.pref_title_virtual_pump_type),

    NsClientUrl(key = "nsclientinternal_url", defaultValue = "", titleResId = R.string.pref_title_ns_url),
    NsClientApiSecret(key = "nsclientinternal_api_secret", defaultValue = "", titleResId = R.string.pref_title_ns_api_secret, isPassword = true),
    NsClientWifiSsids(key = "ns_wifi_ssids", defaultValue = "", titleResId = R.string.pref_title_ns_wifi_ssids, dependency = BooleanKey.NsClientUseWifi),
    NsClientAccessToken(key = "nsclient_token", defaultValue = "", titleResId = R.string.pref_title_ns_access_token, isPassword = true),

    PumpCommonBolusStorage(key = "pump_sync_storage_bolus", defaultValue = ""),
    PumpCommonTbrStorage(key = "pump_sync_storage_tbr", defaultValue = ""),
}
