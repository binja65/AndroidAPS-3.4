package com.nightscout.eversense.packets.e3.util

import java.util.GregorianCalendar

class EversenseE3Parser {
    companion object {
        fun readDate(data: ByteArray, start: Int): Long {
            val day = data[start].toInt() and 31
            var month = data[start].toInt() shr 5
            val year = (data[start + 1].toInt() shr 1) + 2000

            if (data[start + 1] % 2 == 1) {
                month += 8
            }

            val date = GregorianCalendar(year, month - 1, day, 0, 0, 0).getTime()
            return date.time
        }

        fun readTime(data: ByteArray, start: Int): Long {
            val hour = data[start + 1].toInt() shr 3
            val minute = ((data[start + 1].toInt() and 7) shl 3) or (data[start].toInt() shr 5)
            val second = (data[start].toInt() and 31) * 2

            val date = GregorianCalendar(0, 0, 0, hour, minute, second).getTime()
            return date.time
        }

        fun readGlucose(data: ByteArray, start: Int): Int {
            val lowBit = data[start].toInt()
            val highBit = data[start+1].toInt() shl 8
            return lowBit or highBit
        }
    }
}