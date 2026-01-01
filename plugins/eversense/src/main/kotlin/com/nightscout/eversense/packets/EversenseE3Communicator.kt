package com.nightscout.eversense.packets

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.nightscout.eversense.EversenseGattCallback
import com.nightscout.eversense.StorageKeys
import com.nightscout.eversense.callbacks.EversenseWatcher
import com.nightscout.eversense.enums.EversenseTrendArrow
import com.nightscout.eversense.enums.EversenseType
import com.nightscout.eversense.packets.e3.GetRecentGlucoseDatePacket
import com.nightscout.eversense.packets.e3.GetRecentGlucoseTimePacket
import com.nightscout.eversense.packets.e3.GetRecentGlucoseValuePacket
import com.nightscout.eversense.packets.e3.GetCurrentDatetimePacket
import com.nightscout.eversense.packets.e3.SetCurrentDatetimePacket
import java.util.concurrent.TimeUnit

class EversenseE3Communicator {
    companion object {
        private const val TAG = "EversenseE3Communicator"

        fun readGlucose(gatt: EversenseGattCallback, preferences: SharedPreferences, watchers: List<EversenseWatcher>) {
            val fourHalfMinAgo = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(270)
            val recentGlucoseDatetime = preferences.getLong(StorageKeys.RECENT_GLUCOSE_DATETIME_KEY, 0)

            if (fourHalfMinAgo < recentGlucoseDatetime) {
                Log.w(TAG, "Glucose data is still recent - lastReading: $recentGlucoseDatetime")
                return
            }

            try {
                val recentDate = gatt.writePacket<GetRecentGlucoseDatePacket.Response>(GetRecentGlucoseDatePacket())
                val recentTime = gatt.writePacket<GetRecentGlucoseTimePacket.Response>(GetRecentGlucoseTimePacket())

                val recentDatetime = recentDate.date + recentTime.time
                if (recentDatetime <= recentGlucoseDatetime) {
                    Log.w(TAG, "Glucose data is still recent after reading - currentReading: $recentDatetime, lastReading: $recentGlucoseDatetime")
                    return
                }

                val recentGlucose = gatt.writePacket<GetRecentGlucoseValuePacket.Response>(GetRecentGlucoseValuePacket())
                if (recentGlucose.glucoseInMgDl > 1000) {
                    Log.e(TAG, "recentGlucose exceeds range - received: ${recentGlucose.glucoseInMgDl}")
                    return
                }

                // TODO: read history for backfill

                preferences.edit(commit = true) {
                    putLong(StorageKeys.RECENT_GLUCOSE_DATETIME_KEY, recentDatetime)
                    putInt(StorageKeys.RECENT_GLUCOSE_VALUE_KEY, recentGlucose.glucoseInMgDl)
                }

                watchers.forEach {
                    it.onCGMRead(EversenseType.EVERSENSE_E3, recentGlucose.glucoseInMgDl, recentDatetime, EversenseTrendArrow.FLAT)
                }
            } catch (exception: Exception) {
                Log.e(TAG, "Got exception during readGlucose - exception $exception")
            }
        }

        fun fullSync(gatt: EversenseGattCallback, preferences: SharedPreferences) {
            // TODO: Read all MemoryMap's from transmitter relevant for this plugin
            val currentDatetime = gatt.writePacket<GetCurrentDatetimePacket.Response>(GetCurrentDatetimePacket())
            if (currentDatetime.needsTimeSync) {
                gatt.writePacket<SetCurrentDatetimePacket.Response>(SetCurrentDatetimePacket())
            }
        }
    }
}