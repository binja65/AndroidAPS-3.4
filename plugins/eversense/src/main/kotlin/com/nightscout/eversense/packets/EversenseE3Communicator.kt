package com.nightscout.eversense.packets

import android.content.SharedPreferences
import android.util.Log
import com.nightscout.eversense.EversenseGattCallback
import com.nightscout.eversense.StorageKeys
import com.nightscout.eversense.callbacks.EversenseWatcher
import com.nightscout.eversense.enums.EversenseTrendArrow
import com.nightscout.eversense.enums.EversenseType
import com.nightscout.eversense.packets.e3.GetRecentGlucoseDate
import com.nightscout.eversense.packets.e3.GetRecentGlucoseTime
import com.nightscout.eversense.packets.e3.GetRecentGlucoseValue
import java.util.Date
import java.util.concurrent.TimeUnit

class EversenseE3Communicator {
    companion object {
        private const val TAG = "EversenseE3Communicator"

        fun readGlucose(gatt: EversenseGattCallback, preferences: SharedPreferences, watchers: List<EversenseWatcher>) {
            val fourHalfMinAgo = Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(270))
            val recentGlucoseDatetime = Date(preferences.getLong(StorageKeys.RECENT_GLUCOSE_DATETIME_KEY, 0))
            if (fourHalfMinAgo.before(recentGlucoseDatetime)) {
                Log.i(TAG, "Glucose data is still recent - lastReading: $recentGlucoseDatetime")
                return
            }

            try {
                val recentDate = gatt.writePacket<GetRecentGlucoseDate.Response>(GetRecentGlucoseDate())
                val recentTime = gatt.writePacket<GetRecentGlucoseTime.Response>(GetRecentGlucoseTime())

                // TODO: Check date & time parsing...
                val recentDatetime = Date(recentDate.date + recentTime.time)
                if (recentDatetime.before(recentGlucoseDatetime)) {
                    Log.i(TAG, "Glucose data is still recent after reading - currentReading: $recentDatetime, lastReading: $recentGlucoseDatetime")
                    return
                }

                val recentGlucose = gatt.writePacket<GetRecentGlucoseValue.Response>(GetRecentGlucoseValue())
                if (recentGlucose.glucoseInMgDl > 1000) {
                    Log.w(TAG, "recentGlucose exceeds range - received: ${recentGlucose.glucoseInMgDl}")
                    return
                }

                // TODO: read history for backfill

                val editor = preferences.edit()
                editor.putLong(StorageKeys.RECENT_GLUCOSE_DATETIME_KEY, recentDatetime.time)
                editor.putInt(StorageKeys.RECENT_GLUCOSE_VALUE_KEY, recentGlucose.glucoseInMgDl)
                editor.commit()

                watchers.forEach {
                    it.onCGMRead(EversenseType.EVERSENSE_E3, recentGlucose.glucoseInMgDl, recentDatetime.time, EversenseTrendArrow.FLAT)
                }
            } catch (exception: Exception) {
                Log.e(TAG, "Got exception during readGlucose - exception $exception")
            }
        }

        fun fullSync(gatt: EversenseGattCallback, preferences: SharedPreferences) {
            // TODO: Read all MemoryMap's from transmitter relevant for this plugin
            // Examples: Transmitter settings, transmitter datetime
        }
    }
}