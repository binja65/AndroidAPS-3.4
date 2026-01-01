package com.nightscout.eversense.packets.e3.util

import android.util.Log
import java.util.GregorianCalendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class EversenseE3Parser {
    companion object {
        fun readDate(data: ByteArray, start: Int): Long {
            val lowBit = data[start].toUByte().toUInt()
            val highBit = data[start+1].toUByte().toUInt()

            val day = lowBit and 31u
            var month = lowBit shr 5
            val year = (highBit shr 1) + 2000u

            if (highBit and 1u == 1u) {
                month += 8u
            }

            val date = GregorianCalendar(year.toInt(), (month - 1u).toInt(), day.toInt(), 0, 0, 0).getTime()
            return date.time
        }

        fun readTime(data: ByteArray, start: Int): Long {
            val lowBit = data[start].toUByte().toUInt()
            val highBit = data[start+1].toUByte().toUInt()

            val hour = (highBit shr 3).toLong()
            val minute = (((highBit and 7u) shl 3) or (lowBit shr 5)).toLong()
            val second = ((lowBit and 31u) * 2u).toLong()

            return TimeUnit.HOURS.toMillis(hour) +
                TimeUnit.MINUTES.toMillis(minute) +
                TimeUnit.SECONDS.toMillis(second)
        }

        fun readTimezone(data: ByteArray, start: Int): Long {
            var timezoneOffset = readTime(data, start)
            if (data[start + 2] != 0.toByte()) {
                timezoneOffset *= -1
            }

            return timezoneOffset
        }

        fun readGlucose(data: ByteArray, start: Int): Int {
            val lowBit =  data[start].toUByte().toInt()
            val highBit = data[start+1].toUByte().toInt() shl 8
            return lowBit or highBit
        }
    }
}