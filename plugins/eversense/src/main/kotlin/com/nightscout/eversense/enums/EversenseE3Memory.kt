package com.nightscout.eversense.enums

enum class EversenseE3Memory(private val address: Long) {
    RecentGlucoseDate(0x0000_0410),
    RecentGlucoseTime(0x0000_0412),
    RecentGlucoseValue(0x0000_0414);

    fun getRequestData(): ByteArray {
        return byteArrayOf(
            this.address.toByte(),
            (this.address shr 8).toByte(),
            (this.address shr 16).toByte(),
        )
    }
}