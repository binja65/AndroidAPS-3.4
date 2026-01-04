package com.nightscout.eversense.models

import kotlinx.serialization.Serializable

@Serializable
class EversenseState {
    var lastSync: Long = 0
    var batteryPrecentage: Int = 0
    var recentGlucoseDatetime: Long = 0
    var recentGlucoseValue: Int = 0
}
