package com.nightscout.eversense.enums

enum class EversenseE3Memory(private val address: Long) {
    BatteryPercentage(0x0000_0406),
    RecentGlucoseDate(0x0000_0410),
    RecentGlucoseTime(0x0000_0412),
    RecentGlucoseValue(0x0000_0414),
    VibrateMode(0x0000_0902),
    LowGlucoseTarget(0x0000_1102),
    HighGlucoseTarget(0x0000_1104),
    HighGlucoseAlarmEnabled(0x0000_1029),
    HighGlucoseAlarmThreshold(0x0000_110C),
    LowGlucoseAlarmThreshold(0x0000_110A),
    PredictiveAlert(0x0000_1020),
    PredictiveFallingTime(0x0000_1021),
    PredictiveRisingTime(0x0000_1022),
    PredictiveLowAlert(0x0000_1027),
    PredictiveHighAlert(0x0000_1028),
    RateAlert(0x0000_1010),
    RateFallingAlert(0x0000_1025),
    RateRisingAlert(0x0000_1026),
    RateFallingThreshold(0x0000_1011),
    RateRisingThreshold(0x0000_1012);

    fun getRequestData(): ByteArray {
        return byteArrayOf(
            this.address.toByte(),
            (this.address shr 8).toByte(),
            (this.address shr 16).toByte(),
        )
    }
}