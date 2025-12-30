package com.nightscout.eversense.callbacks

import com.nightscout.eversense.enums.EversenseTrendArrow
import com.nightscout.eversense.enums.EversenseType

interface EversenseWatcher {
    fun onCGMRead(type: EversenseType, glucoseInMgDl: Int, datetime: Long, trend: EversenseTrendArrow)
}