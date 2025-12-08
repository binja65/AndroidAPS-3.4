package app.aaps.wear.complications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat

/*
 * Created by dlvoy on 2019-11-12
 * Modified to use foreground service for reliable tap handling
 */
class ComplicationTapBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras ?: return
        val provider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            extras.getParcelable(EXTRA_PROVIDER_COMPONENT, ComponentName::class.java)
        } else {
            @Suppress("DEPRECATION")
            extras.getParcelable(EXTRA_PROVIDER_COMPONENT)
        }
        val complicationId = extras.getInt(EXTRA_COMPLICATION_ID)
        val complicationAction = extras.getString(EXTRA_COMPLICATION_ACTION, ComplicationAction.MENU.toString())
        val since = if (extras.containsKey(EXTRA_COMPLICATION_SINCE)) extras.getLong(EXTRA_COMPLICATION_SINCE) else null

        var action = ComplicationAction.MENU
        try {
            action = ComplicationAction.valueOf(complicationAction)
        } catch (_: IllegalArgumentException) {
            // ignore, use default
        } catch (_: NullPointerException) {
            // ignore, use default
        }

        // Start foreground service to handle the tap action reliably
        // This ensures the action works even when the app process was killed
        val serviceIntent = ComplicationTapService.createIntent(context, provider, complicationId, action, since)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    companion object {

        private const val EXTRA_PROVIDER_COMPONENT = "info.nightscout.androidaps.complications.action.PROVIDER_COMPONENT"
        private const val EXTRA_COMPLICATION_ID = "info.nightscout.androidaps.complications.action.COMPLICATION_ID"
        private const val EXTRA_COMPLICATION_ACTION = "info.nightscout.androidaps.complications.action.COMPLICATION_ACTION"
        private const val EXTRA_COMPLICATION_SINCE = "info.nightscout.androidaps.complications.action.COMPLICATION_SINCE"

        /**
         * Returns a pending intent, suitable for use as a tap intent, that causes a complication to be
         * toggled and updated.
         */
        fun getTapActionIntent(
            context: Context, provider: ComponentName?, complicationId: Int, action: ComplicationAction
        ): PendingIntent {
            val intent = Intent(context, ComplicationTapBroadcastReceiver::class.java)
            intent.putExtra(EXTRA_PROVIDER_COMPONENT, provider)
            intent.putExtra(EXTRA_COMPLICATION_ID, complicationId)
            intent.putExtra(EXTRA_COMPLICATION_ACTION, action.toString())

            // Pass complicationId as the requestCode to ensure that different complications get
            // different intents.
            return PendingIntent.getBroadcast(
                context, complicationId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        /**
         * Returns a pending intent, suitable for use as a tap intent, that causes a complication to be
         * toggled and updated.
         */
        fun getTapWarningSinceIntent(
            context: Context?, provider: ComponentName?, complicationId: Int, action: ComplicationAction, since: Long
        ): PendingIntent {
            val intent = Intent(context, ComplicationTapBroadcastReceiver::class.java)
            intent.putExtra(EXTRA_PROVIDER_COMPONENT, provider)
            intent.putExtra(EXTRA_COMPLICATION_ID, complicationId)
            intent.putExtra(EXTRA_COMPLICATION_ACTION, action.toString())
            intent.putExtra(EXTRA_COMPLICATION_SINCE, since)

            // Pass complicationId as the requestCode to ensure that different complications get
            // different intents.
            return PendingIntent.getBroadcast(
                context, complicationId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}