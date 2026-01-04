package com.nightscout.eversense.packets

import android.content.SharedPreferences
import androidx.core.content.edit
import com.nightscout.eversense.EversenseGattCallback
import com.nightscout.eversense.EversenseLogger
import com.nightscout.eversense.StorageKeys
import com.nightscout.eversense.callbacks.EversenseWatcher
import com.nightscout.eversense.enums.EversenseTrendArrow
import com.nightscout.eversense.enums.EversenseType
import com.nightscout.eversense.models.EversenseState
import com.nightscout.eversense.packets.e3.GetBatteryPercentagePacket
import com.nightscout.eversense.packets.e3.GetRecentGlucoseDatePacket
import com.nightscout.eversense.packets.e3.GetRecentGlucoseTimePacket
import com.nightscout.eversense.packets.e3.GetRecentGlucoseValuePacket
import com.nightscout.eversense.packets.e3.GetCurrentDatetimePacket
import com.nightscout.eversense.packets.e3.SetCurrentDatetimePacket
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class EversenseE3Communicator {
    companion object {
        private const val TAG = "EversenseE3Communicator"

        fun readGlucose(gatt: EversenseGattCallback, preferences: SharedPreferences, watchers: List<EversenseWatcher>) {
            val stateJson = preferences.getString(StorageKeys.STATE, null) ?: "{}"
            val state = Json.decodeFromString<EversenseState>(stateJson)
            val fourHalfMinAgo = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(270)

            if (fourHalfMinAgo < state.recentGlucoseDatetime) {
                EversenseLogger.warning(TAG, "Glucose data is still recent - lastReading: $state.recentGlucoseDatetime")
                return
            }

            try {
                val recentDate = gatt.writePacket<GetRecentGlucoseDatePacket.Response>(GetRecentGlucoseDatePacket())
                val recentTime = gatt.writePacket<GetRecentGlucoseTimePacket.Response>(GetRecentGlucoseTimePacket())

                val recentDatetime = recentDate.date + recentTime.time
                if (recentDatetime <= state.recentGlucoseDatetime) {
                    EversenseLogger.warning(TAG, "Glucose data is still recent after reading - currentReading: $recentDatetime, lastReading: $state.recentGlucoseDatetime")
                    return
                }

                val recentGlucose = gatt.writePacket<GetRecentGlucoseValuePacket.Response>(GetRecentGlucoseValuePacket())
                if (recentGlucose.glucoseInMgDl > 1000) {
                    EversenseLogger.error(TAG, "recentGlucose exceeds range - received: ${recentGlucose.glucoseInMgDl}")
                    return
                }

                state.recentGlucoseDatetime = recentDatetime
                state.recentGlucoseValue = recentGlucose.glucoseInMgDl

                // TODO: read history for backfill

                preferences.edit(commit = true) {
                    putString(StorageKeys.STATE, Json.encodeToString(state))
                }

                watchers.forEach {
                    it.onCGMRead(EversenseType.EVERSENSE_E3, recentGlucose.glucoseInMgDl, recentDatetime, EversenseTrendArrow.FLAT)
                }
            } catch (exception: Exception) {
                EversenseLogger.error(TAG, "Got exception during readGlucose - exception $exception")
            }
        }

        fun fullSync(gatt: EversenseGattCallback, preferences: SharedPreferences, watchers: List<EversenseWatcher>) {
            try {
                val stateJson = preferences.getString(StorageKeys.STATE, null) ?: "{}"
                val state = Json.decodeFromString<EversenseState>(stateJson)

                val currentDatetime = gatt.writePacket<GetCurrentDatetimePacket.Response>(GetCurrentDatetimePacket())
                if (currentDatetime.needsTimeSync) {
                    gatt.writePacket<SetCurrentDatetimePacket.Response>(SetCurrentDatetimePacket())
                }

                val batteryPercentage = gatt.writePacket<GetBatteryPercentagePacket.Response>(GetBatteryPercentagePacket())
                state.batteryPrecentage = batteryPercentage.percentage

                state.lastSync = System.currentTimeMillis()
                preferences.edit(commit = true) {
                    putString(StorageKeys.STATE, Json.encodeToString(state))
                }
                watchers.forEach {
                    it.onStateChanged(state)
                }
            } catch (exception: Exception) {
                EversenseLogger.error(TAG, "Failed to do full sync: $exception")
            }
        }
    }
}