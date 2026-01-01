package app.aaps.pump.omnipod.eros

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.interfaces.PreferenceKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.NavigablePreferenceContent
import app.aaps.core.ui.compose.preference.Preference
import app.aaps.core.ui.compose.preference.PreferenceSectionState
import app.aaps.core.ui.compose.preference.PreferenceSubScreen
import app.aaps.pump.common.hw.rileylink.keys.RileylinkBooleanPreferenceKey
import app.aaps.pump.omnipod.common.keys.OmnipodBooleanPreferenceKey
import app.aaps.pump.omnipod.common.keys.OmnipodIntPreferenceKey
import app.aaps.pump.omnipod.eros.keys.ErosBooleanPreferenceKey

/**
 * Compose implementation of Omnipod Eros preferences.
 * Uses key-based rendering - UI is auto-generated from preference keys.
 */
class OmnipodErosPreferencesCompose(
    private val preferences: Preferences,
    private val config: Config,
    private val rileyLinkConfigActivityClass: Class<out Activity>? = null
) : NavigablePreferenceContent {

    override val titleResId: Int = R.string.omnipod_eros_name

    private val rileyLinkKeys: List<PreferenceKey> = listOf(
        RileylinkBooleanPreferenceKey.OrangeUseScanning,
        RileylinkBooleanPreferenceKey.ShowReportedBatteryLevel,
        ErosBooleanPreferenceKey.BatteryChangeLogging
    )

    private val beepKeys: List<PreferenceKey> = listOf(
        OmnipodBooleanPreferenceKey.BolusBeepsEnabled,
        OmnipodBooleanPreferenceKey.BasalBeepsEnabled,
        OmnipodBooleanPreferenceKey.SmbBeepsEnabled,
        OmnipodBooleanPreferenceKey.TbrBeepsEnabled
    )

    private val alertKeys: List<PreferenceKey> = listOf(
        OmnipodBooleanPreferenceKey.ExpirationReminder,
        OmnipodIntPreferenceKey.ExpirationAlarmHours,
        OmnipodBooleanPreferenceKey.LowReservoirAlert,
        OmnipodIntPreferenceKey.LowReservoirAlertUnits,
        OmnipodBooleanPreferenceKey.AutomaticallyAcknowledgeAlerts
    )

    private val notificationKeys: List<PreferenceKey> = listOf(
        OmnipodBooleanPreferenceKey.SoundUncertainTbrNotification,
        OmnipodBooleanPreferenceKey.SoundUncertainSmbNotification,
        OmnipodBooleanPreferenceKey.SoundUncertainBolusNotification
    )

    private val otherKeys: List<PreferenceKey> = listOf(
        ErosBooleanPreferenceKey.ShowSuspendDeliveryButton,
        ErosBooleanPreferenceKey.ShowPulseLogButton,
        ErosBooleanPreferenceKey.ShowRileyLinkStatsButton,
        ErosBooleanPreferenceKey.TimeChangeEnabled
    )

    override val mainContent: ((@Composable (PreferenceSectionState?) -> Unit))? = null

    override val subscreens: List<PreferenceSubScreen> = listOf(
        PreferenceSubScreen(
            key = "omnipod_eros_riley_link",
            titleResId = R.string.omnipod_eros_preferences_category_riley_link,
            keys = rileyLinkKeys
        ) { _ ->
            // Special RileyLink config activity launcher
            rileyLinkConfigActivityClass?.let { activityClass ->
                RileyLinkConfigPreferenceItem(
                    titleResId = app.aaps.pump.common.hw.rileylink.R.string.rileylink_configuration,
                    activityClass = activityClass
                )
            }

            AdaptivePreferenceList(
                keys = rileyLinkKeys,
                preferences = preferences,
                config = config
            )
        },

        PreferenceSubScreen(
            key = "omnipod_eros_beeps",
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
            key = "omnipod_eros_alerts",
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
            key = "omnipod_eros_notifications",
            titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_notifications,
            keys = notificationKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = notificationKeys,
                preferences = preferences,
                config = config
            )
        },

        PreferenceSubScreen(
            key = "omnipod_eros_other",
            titleResId = app.aaps.pump.omnipod.common.R.string.omnipod_common_preferences_category_other,
            keys = otherKeys
        ) { _ ->
            AdaptivePreferenceList(
                keys = otherKeys,
                preferences = preferences,
                config = config
            )
        }
    )
}

/**
 * Composable preference for launching RileyLink configuration activity.
 */
@Composable
private fun RileyLinkConfigPreferenceItem(
    titleResId: Int,
    activityClass: Class<out Activity>
) {
    val context = LocalContext.current
    Preference(
        title = { Text(stringResource(titleResId)) },
        onClick = {
            context.startActivity(Intent(context, activityClass))
        }
    )
}
