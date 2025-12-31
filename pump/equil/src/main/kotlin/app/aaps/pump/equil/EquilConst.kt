package app.aaps.pump.equil

object EquilConst {

    const val EQUIL_CMD_TIME_OUT: Long = 300000
    const val EQUIL_BLE_WRITE_TIME_OUT: Long = 20
    const val EQUIL_BLE_NEXT_CMD: Long = 150
    const val EQUIL_SUPPORT_LEVEL = 5.3f
    const val EQUIL_SUPPORT_LEVEL_PUMP_V1_0 = 5.3f  // 1.0 PUMP
    const val EQUIL_SUPPORT_LEVEL_PUMP_V1_5 = 1.3f  // 1.5 PUMP
    const val EQUIL_SUPPORT_LEVEL_PUMP_V1_5R = 2.3f  // 1.5R PUMP
    const val EQUIL_BOLUS_THRESHOLD_STEP = 1600
    const val EQUIL_BASAL_THRESHOLD_STEP = 240
    const val EQUIL_STEP_MAX = 32000
    const val EQUIL_STEP_FILL = 160
    const val EQUIL_STEP_AIR = 120
}
