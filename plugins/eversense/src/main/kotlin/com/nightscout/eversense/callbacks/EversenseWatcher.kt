package com.nightscout.eversense.callbacks

import com.nightscout.eversense.enums.EversenseTrendArrow
import com.nightscout.eversense.enums.EversenseType
import com.nightscout.eversense.models.EversenseState

interface EversenseWatcher {
    fun onCGMRead(type: EversenseType, glucoseInMgDl: Int, datetime: Long, trend: EversenseTrendArrow)
    fun onStateChanged(state: EversenseState)
}