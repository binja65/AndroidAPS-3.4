@file:Suppress("DEPRECATION")

package app.aaps.wear.complications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.wearable.complications.ProviderUpdateRequester
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.logging.LTag
import app.aaps.core.interfaces.sharedPreferences.SP
import app.aaps.wear.R
import app.aaps.wear.interaction.actions.ECarbActivity
import app.aaps.wear.interaction.actions.TreatmentActivity
import app.aaps.wear.interaction.actions.WizardActivity
import app.aaps.wear.interaction.menus.MainMenuActivity
import app.aaps.wear.interaction.menus.StatusMenuActivity
import app.aaps.wear.interaction.utils.Constants
import app.aaps.wear.interaction.utils.DisplayFormat
import app.aaps.wear.interaction.utils.WearUtil
import dagger.android.DaggerService
import javax.inject.Inject

/**
 * Foreground service to handle complication tap actions reliably.
 * This ensures the tap action works even when the app process was killed.
 */
class ComplicationTapService : DaggerService() {

    @Inject lateinit var wearUtil: WearUtil
    @Inject lateinit var displayFormat: DisplayFormat
    @Inject lateinit var sp: SP
    @Inject lateinit var aapsLogger: AAPSLogger

    companion object {
        private const val CHANNEL_ID = "complication_tap_channel"
        private const val NOTIFICATION_ID = 29847

        const val EXTRA_PROVIDER_COMPONENT = "info.nightscout.androidaps.complications.action.PROVIDER_COMPONENT"
        const val EXTRA_COMPLICATION_ID = "info.nightscout.androidaps.complications.action.COMPLICATION_ID"
        const val EXTRA_COMPLICATION_ACTION = "info.nightscout.androidaps.complications.action.COMPLICATION_ACTION"
        const val EXTRA_COMPLICATION_SINCE = "info.nightscout.androidaps.complications.action.COMPLICATION_SINCE"

        fun createIntent(
            context: Context,
            provider: ComponentName?,
            complicationId: Int,
            action: ComplicationAction,
            since: Long? = null
        ): Intent {
            return Intent(context, ComplicationTapService::class.java).apply {
                putExtra(EXTRA_PROVIDER_COMPONENT, provider)
                putExtra(EXTRA_COMPLICATION_ID, complicationId)
                putExtra(EXTRA_COMPLICATION_ACTION, action.toString())
                since?.let { putExtra(EXTRA_COMPLICATION_SINCE, it) }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start as foreground service immediately to prevent ANR
        startForeground(NOTIFICATION_ID, createNotification())

        try {
            intent?.let { handleComplicationTap(it) }
        } catch (e: Exception) {
            aapsLogger.error(LTag.WEAR, "Error handling complication tap", e)
        } finally {
            // Stop the service after handling the tap
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf(startId)
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun handleComplicationTap(intent: Intent) {
        val extras = intent.extras ?: return
        val provider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            extras.getParcelable(EXTRA_PROVIDER_COMPONENT, ComponentName::class.java)
        } else {
            @Suppress("DEPRECATION")
            extras.getParcelable(EXTRA_PROVIDER_COMPONENT)
        }
        val complicationId = extras.getInt(EXTRA_COMPLICATION_ID)
        val complicationActionStr = extras.getString(EXTRA_COMPLICATION_ACTION, ComplicationAction.MENU.toString())

        var action = ComplicationAction.MENU
        try {
            action = ComplicationAction.valueOf(complicationActionStr)
        } catch (_: IllegalArgumentException) {
            aapsLogger.error(LTag.WEAR, "Cannot interpret complication action: $complicationActionStr")
        } catch (ex: NullPointerException) {
            aapsLogger.error(LTag.WEAR, "Cannot interpret complication action: $complicationActionStr")
        }

        action = remapActionWithUserPreferences(action)
        aapsLogger.debug(LTag.WEAR, "ComplicationTapService handling action: $action for complication: $complicationId")

        // Request an update for the complication that has just been tapped
        if (provider != null) {
            val requester = ProviderUpdateRequester(this, provider)
            requester.requestUpdate(complicationId)
        }

        var intentOpen: Intent? = null
        when (action) {
            ComplicationAction.NONE -> return

            ComplicationAction.WIZARD -> intentOpen = Intent(this, WizardActivity::class.java)
            ComplicationAction.BOLUS -> intentOpen = Intent(this, TreatmentActivity::class.java)
            ComplicationAction.E_CARB -> intentOpen = Intent(this, ECarbActivity::class.java)
            ComplicationAction.STATUS -> intentOpen = Intent(this, StatusMenuActivity::class.java)

            ComplicationAction.WARNING_OLD, ComplicationAction.WARNING_SYNC -> {
                val oneAndHalfMinuteAgo = wearUtil.timestamp() - (Constants.MINUTE_IN_MS + Constants.SECOND_IN_MS * 30)
                val since = extras.getLong(EXTRA_COMPLICATION_SINCE, oneAndHalfMinuteAgo)
                @StringRes val labelId = if (action == ComplicationAction.WARNING_SYNC) R.string.msg_warning_sync else R.string.msg_warning_old
                val msg = String.format(getString(labelId), displayFormat.shortTimeSince(since))
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }

            ComplicationAction.MENU -> intentOpen = Intent(this, MainMenuActivity::class.java)
        }

        if (intentOpen != null) {
            intentOpen.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentOpen)
        }
    }

    private val complicationTapAction: String
        get() = sp.getString(R.string.key_complication_tap_action, "default")

    private fun remapActionWithUserPreferences(originalAction: ComplicationAction): ComplicationAction {
        val userPrefAction = complicationTapAction
        return when (originalAction) {
            ComplicationAction.WARNING_OLD, ComplicationAction.WARNING_SYNC -> originalAction

            else -> when (userPrefAction) {
                "menu" -> ComplicationAction.MENU
                "wizard" -> ComplicationAction.WIZARD
                "bolus" -> ComplicationAction.BOLUS
                "ecarb" -> ComplicationAction.E_CARB
                "status" -> ComplicationAction.STATUS
                "none" -> ComplicationAction.NONE
                "default" -> originalAction
                else -> originalAction
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Complication Actions",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Handles complication tap actions"
            setShowBadge(false)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_icon)
            .setContentTitle("AAPS")
            .setContentText("Processing...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
