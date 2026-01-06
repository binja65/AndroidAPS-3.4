package app.aaps.pump.omnipod.dash

// TODO: Remove after full migration to new Compose preferences (PreferenceSubScreenDef)
// Replace this custom Compose UI with declarative preference definitions in the plugin's getPreferenceScreenContent()

import androidx.compose.runtime.Composable
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.omnipod.common.keys.OmnipodBooleanPreferenceKey
import app.aaps.pump.omnipod.common.keys.OmnipodIntPreferenceKey
import app.aaps.pump.omnipod.dash.keys.DashBooleanPreferenceKey

/**
 * Compose implementation of Omnipod DASH preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class OmnipodDashPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.omnipod_dash_name

    private val beepKeys: List<PreferenceKey> = listOf(
        OmnipodBooleanPreferenceKey.BolusBeepsEnabled,
        OmnipodBooleanPreferenceKey.BasalBeepsEnabled,
        OmnipodBooleanPreferenceKey.SmbBeepsEnabled,
        OmnipodBooleanPreferenceKey.TbrBeepsEnabled,
        DashBooleanPreferenceKey.UseBonding
    )

    private val alertKeys: List<PreferenceKey> = listOf(
        OmnipodBooleanPreferenceKey.ExpirationReminder,
        OmnipodIntPreferenceKey.ExpirationReminderHours,
        OmnipodBooleanPreferenceKey.ExpirationAlarm,
        OmnipodIntPreferenceKey.ExpirationAlarmHours,
        OmnipodBooleanPreferenceKey.LowReservoirAlert,
        OmnipodIntPreferenceKey.LowReservoirAlertUnits
    )

    private val notificationKeys: List<PreferenceKey> = listOf(
        OmnipodBooleanPreferenceKey.SoundUncertainTbrNotification,
        OmnipodBooleanPreferenceKey.SoundUncertainSmbNotification,
        OmnipodBooleanPreferenceKey.SoundUncertainBolusNotification,
        DashBooleanPreferenceKey.SoundDeliverySuspendedNotification
    )

    override val mainContent: ((@Composable (PreferenceSectionState?) -> Unit))? = null

    override val subscreens: List<PreferenceSubScreen> = listOf(
        PreferenceSubScreen(
            key = "omnipod_dash_beeps",
            titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_confirmation_beeps,
            keys = beepKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = beepKeys,
                preferences = preferences,
                config = config
            )
        },

        PreferenceSubScreen(
            key = "omnipod_dash_alerts",
            titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_alerts,
            keys = alertKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = alertKeys,
                preferences = preferences,
                config = config
            )
        },

        PreferenceSubScreen(
            key = "omnipod_dash_notifications",
            titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_notifications,
            keys = notificationKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = notificationKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}
