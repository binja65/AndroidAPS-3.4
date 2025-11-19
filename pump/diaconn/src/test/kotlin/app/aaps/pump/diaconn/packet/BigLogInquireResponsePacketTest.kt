package app.aaps.pump.diaconn.packet

import app.aaps.core.data.model.TE
import app.aaps.core.data.pump.defs.PumpType
import app.aaps.core.interfaces.pump.DetailedBolusInfoStorage
import app.aaps.core.interfaces.pump.PumpSync
import app.aaps.core.interfaces.pump.TemporaryBasalStorage
import app.aaps.pump.common.utils.and
import app.aaps.pump.diaconn.DiaconnG8Pump
import app.aaps.pump.diaconn.api.DiaconnLogUploader
import app.aaps.pump.diaconn.database.DiaconnHistoryRecordDao
import app.aaps.pump.diaconn.keys.DiaconnBooleanKey
import app.aaps.shared.tests.TestBaseWithProfile
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.anyDouble
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.anyString
import org.mockito.Mockito.eq
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class BigLogInquireResponsePacketTest : TestBaseWithProfile() {

    @Mock lateinit var detailedBolusInfoStorage: DetailedBolusInfoStorage
    @Mock lateinit var temporaryBasalStorage: TemporaryBasalStorage
    @Mock lateinit var pumpSync: PumpSync
    @Mock lateinit var diaconnHistoryRecordDao: DiaconnHistoryRecordDao
    @Mock lateinit var diaconnLogUploader: DiaconnLogUploader

    private lateinit var diaconnG8Pump: DiaconnG8Pump

    private val packetInjector = HasAndroidInjector {
        AndroidInjector {
            if (it is BigLogInquireResponsePacket) {
                it.aapsLogger = aapsLogger
                it.dateUtil = dateUtil
                it.rxBus = rxBus
                it.rh = rh
                it.activePlugin = activePlugin
                it.diaconnG8Pump = diaconnG8Pump
                it.detailedBolusInfoStorage = detailedBolusInfoStorage
                it.temporaryBasalStorage = temporaryBasalStorage
                it.preferences = preferences
                it.pumpSync = pumpSync
                it.diaconnHistoryRecordDao = diaconnHistoryRecordDao
                it.diaconnLogUploader = diaconnLogUploader
                it.context = context
            }
        }
    }

    @BeforeEach
    fun setup() {
        diaconnG8Pump = DiaconnG8Pump(aapsLogger, dateUtil, decimalFormatter)
    }

    @Test
    fun handleMessageShouldProcessValidResponse() {
        // Given - Valid response packet with 0 logs
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createValidPacket(logCount = 0)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
    }

    @Test
    fun handleMessageShouldFailOnDefectivePacket() {
        // Given
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = ByteArray(50)
        data[0] = 0x00 // Wrong SOP

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun handleMessageShouldFailOnInvalidResult() {
        // Given
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithResult(17) // Invalid result

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun msgTypeShouldBeCorrect() {
        // Given
        val packet = BigLogInquireResponsePacket(packetInjector)

        // Then
        assertThat(packet.msgType).isEqualTo(0xb2.toByte())
    }

    @Test
    fun friendlyNameShouldBeCorrect() {
        // Given
        val packet = BigLogInquireResponsePacket(packetInjector)

        // Then
        assertThat(packet.friendlyName).isEqualTo("BIG_LOG_INQUIRE_RESPONSE")
    }

    @Test
    fun packetShouldAcceptExactly20Bytes() {
        // Given - Standard packet size
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createValidPacket(0)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        assertThat(data.size).isEqualTo(20) // Standard SOP packet size
    }

    @Test
    fun handleMessageShouldFailOnParameterError() {
        // Given - Result code 18 indicates parameter error
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithResult(18)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun handleMessageShouldFailOnProtocolError() {
        // Given - Result code 19 indicates protocol specification error
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithResult(19)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun handleMessageShouldFailOnSystemError() {
        // Given - Any result code other than 16 that's not 17-19 is system error
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithResult(20)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun handleMessageShouldFailOnInvalidCrc() {
        // Given - Valid packet structure but invalid CRC
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createValidPacket(0)
        data[19] = 0xFF.toByte() // Corrupt CRC

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun handleMessageShouldFailOnInvalidPacketSize() {
        // Given - Packet size that's not 20 or 182 bytes
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = ByteArray(25)
        data[0] = 0xef.toByte() // Valid SOP
        data[1] = 0xb2.toByte()
        data[2] = 0x01.toByte()
        data[3] = 0x00.toByte()
        data[4] = 16.toByte() // Valid result
        data[24] = DiaconnG8Packet.getCRC(data, 24)

        // When
        packet.handleMessage(data)

        // Then - Should fail due to invalid packet size
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun handleMessageShouldAcceptBigPacketSize() {
        // Given - Big packet (182 bytes)
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = ByteArray(182)
        data[0] = 0xed.toByte() // SOP_BIG
        data[1] = 0xb2.toByte()
        data[2] = 0x01.toByte()
        data[3] = 0x00.toByte()
        data[4] = 16.toByte() // result (success)
        data[5] = 0.toByte() // log count = 0

        // Fill remaining data
        for (i in 6 until 181) {
            data[i] = 0x00.toByte()
        }

        data[181] = DiaconnG8Packet.getCRC(data, 181)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        assertThat(data.size).isEqualTo(182)
    }

    @Test
    fun handleMessageShouldFailOnInvalidSopByte() {
        // Given - Invalid start-of-packet byte
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = ByteArray(20)
        data[0] = 0xaa.toByte() // Invalid SOP (should be 0xef or 0xed)
        data[1] = 0xb2.toByte()
        data[19] = DiaconnG8Packet.getCRC(data, 19)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun handleMessageShouldHandleZeroResult() {
        // Given - Result code 0 (system error)
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithResult(0)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun handleMessageShouldHandleMaxResult() {
        // Given - Maximum byte value result code
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithResult(255)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun handleMessageShouldProcessMealBolusSuccessLog() {
        // Given - Packet with LogInjectMealSuccess (0x08)
        `when`(pumpSync.syncBolusWithPumpId(anyLong(), anyDouble(), any(), anyLong(), any(), anyString())).thenReturn(true)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x08, createTimestamp(), bolusAmount = 5000) // 50.00 U

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(pumpSync).syncBolusWithPumpId(anyLong(), eq(50.0), any(), anyLong(), eq(PumpType.DIACONN_G8), eq("12345"))
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessNormalBolusSuccessLog() {
        // Given - Packet with LogInjectNormalSuccess (0x0A)
        `when`(pumpSync.syncBolusWithPumpId(anyLong(), anyDouble(), any(), anyLong(), any(), anyString())).thenReturn(true)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x0A, createTimestamp(), bolusAmount = 1500) // 15.00 U

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(pumpSync).syncBolusWithPumpId(anyLong(), eq(15.0), any(), anyLong(), eq(PumpType.DIACONN_G8), eq("12345"))
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessExtendedBolusStartLog() {
        // Given - Packet with LogSetSquareInjection (0x0C)
        `when`(pumpSync.syncExtendedBolusWithPumpId(anyLong(), anyDouble(), anyLong(), anyBoolean(), anyLong(), any(), anyString())).thenReturn(true)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x0C, createTimestamp(), bolusAmount = 3000, duration = 12) // 30.00 U over 120 min

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(pumpSync).syncExtendedBolusWithPumpId(anyLong(), eq(30.0), anyLong(), eq(false), anyLong(), eq(PumpType.DIACONN_G8), eq("12345"))
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessExtendedBolusStopLog() {
        // Given - Packet with LogInjectSquareFail (0x0E)
        `when`(pumpSync.syncStopExtendedBolusWithPumpId(anyLong(), anyLong(), any(), anyString())).thenReturn(true)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x0E, createTimestamp(), bolusAmount = 2000, reason = 4) // User stop

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(pumpSync).syncStopExtendedBolusWithPumpId(anyLong(), anyLong(), eq(PumpType.DIACONN_G8), eq("12345"))
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessTempBasalStartLog() {
        // Given - Packet with LogTbStartV3 (0x12)
        `when`(pumpSync.syncTemporaryBasalWithPumpId(anyLong(), anyDouble(), anyLong(), anyBoolean(), any(), anyLong(), any(), anyString())).thenReturn(true)
        diaconnG8Pump.serialNo = 12345
        diaconnG8Pump.baseAmount = 1.0 // Base basal rate

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x12, createTimestamp(), tbRate = 51500, tbDuration = 8) // 150% for 120 min

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(pumpSync).syncTemporaryBasalWithPumpId(anyLong(), anyDouble(), anyLong(), eq(true), any(), anyLong(), eq(PumpType.DIACONN_G8), eq("12345"))
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessTempBasalStopLog() {
        // Given - Packet with LogTbStopV3 (0x13)
        `when`(pumpSync.syncStopTemporaryBasalWithPumpId(anyLong(), anyLong(), any(), anyString())).thenReturn(true)
        diaconnG8Pump.serialNo = 12345
        diaconnG8Pump.baseAmount = 1.0

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x13, createTimestamp(), tbRate = 51500, reason = 0) // Complete

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(pumpSync).syncStopTemporaryBasalWithPumpId(anyLong(), anyLong(), eq(PumpType.DIACONN_G8), eq("12345"))
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessSuspendLog() {
        // Given - Packet with LogSuspendV2 (0x03)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x03, createTimestamp(), pattern = 1)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessSuspendReleaseLog() {
        // Given - Packet with LogSuspendReleaseV2 (0x04)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x04, createTimestamp(), pattern = 1)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessInsulinChangeLog() {
        // Given - Packet with LogChangeInjectorSuccess (0x1A)
        `when`(preferences.get(DiaconnBooleanKey.LogInsulinChange)).thenReturn(true)
        `when`(pumpSync.insertTherapyEventIfNewWithTimestamp(anyLong(), any(), anyLong(), any(), anyString())).thenReturn(true)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x1A, createTimestamp(), remainAmount = 30000, primeAmount = 500)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(pumpSync).insertTherapyEventIfNewWithTimestamp(anyLong(), eq(TE.Type.INSULIN_CHANGE), anyLong(), eq(PumpType.DIACONN_G8), anyString())
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessTubeChangeLog() {
        // Given - Packet with LogChangeTubeSuccess (0x18)
        `when`(preferences.get(DiaconnBooleanKey.LogTubeChange)).thenReturn(true)
        `when`(pumpSync.insertTherapyEventIfNewWithTimestamp(anyLong(), any(), anyString(), anyLong(), any(), anyString())).thenReturn(true)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x18, createTimestamp(), remainAmount = 29000, primeAmount = 1000)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(pumpSync).insertTherapyEventIfNewWithTimestamp(anyLong(), eq(TE.Type.NOTE), anyString(), anyLong(), eq(PumpType.DIACONN_G8), anyString())
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessNeedleChangeLog() {
        // Given - Packet with LogChangeNeedleSuccess (0x1C)
        `when`(preferences.get(DiaconnBooleanKey.LogCannulaChange)).thenReturn(true)
        `when`(pumpSync.insertTherapyEventIfNewWithTimestamp(anyLong(), any(), anyLong(), any(), anyString())).thenReturn(true)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x1C, createTimestamp(), remainAmount = 29500, primeAmount = 500)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(pumpSync).insertTherapyEventIfNewWithTimestamp(anyLong(), eq(TE.Type.CANNULA_CHANGE), anyLong(), eq(PumpType.DIACONN_G8), anyString())
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessBatteryAlarmLog() {
        // Given - Packet with LogAlarmBattery (0x28)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x28, createTimestamp(), batteryLevel = 10)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessBlockAlarmLog() {
        // Given - Packet with LogAlarmBlock (0x29)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x29, createTimestamp(), blockAmount = 100, reason = 1)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessInsulinShortageAlarmLog() {
        // Given - Packet with LogAlarmShortAge (0x2A)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x2A, createTimestamp(), remainInsulin = 10)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessSystemResetLog() {
        // Given - Packet with LogResetSysV3 (0x01) with battery replacement reason
        `when`(preferences.get(DiaconnBooleanKey.LogBatteryChange)).thenReturn(true)
        `when`(pumpSync.insertTherapyEventIfNewWithTimestamp(anyLong(), any(), anyLong(), any(), anyString())).thenReturn(true)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x01, createTimestamp(), reason = 3, batteryLevel = 100)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(pumpSync).insertTherapyEventIfNewWithTimestamp(anyLong(), eq(TE.Type.PUMP_BATTERY_CHANGE), anyLong(), eq(PumpType.DIACONN_G8), anyString())
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessDailyBolusLog() {
        // Given - Packet with LogInjection1Day (0x2F)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x2F, createTimestamp(), mealAmount = 10000, extAmount = 5000)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
        verify(pumpSync).createOrUpdateTotalDailyDose(anyLong(), anyDouble(), anyDouble(), eq(0.0), any(), eq(PumpType.DIACONN_G8), eq("12345"))
    }

    @Test
    fun handleMessageShouldProcessDailyBasalLog() {
        // Given - Packet with LogInjection1DayBasal (0x2E)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x2E, createTimestamp(), basalAmount = 2400)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldProcessHourlyBasalLog() {
        // Given - Packet with LogInjection1HourBasal (0x2C)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0x2C, createTimestamp(), beforeAmount = 100, afterAmount = 120)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        verify(diaconnHistoryRecordDao).createOrUpdate(any())
    }

    @Test
    fun handleMessageShouldHandleUnknownLogKind() {
        // Given - Packet with unknown log kind (0xFF)
        diaconnG8Pump.serialNo = 12345

        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithLog(0xFF.toByte(), createTimestamp())

        // When
        packet.handleMessage(data)

        // Then - Should not fail, just skip the log
        assertThat(packet.failed).isFalse()
    }

    private fun createValidPacket(logCount: Int): ByteArray {
        val baseSize = 20
        val logSize = 15 // Each log entry is 15 bytes (1 wrapping + 2 logNum + 12 logData)
        val totalSize = baseSize + (logCount * logSize)
        val data = ByteArray(totalSize)

        data[0] = 0xef.toByte() // SOP
        data[1] = 0xb2.toByte() // msgType
        data[2] = 0x01.toByte() // seq
        data[3] = 0x00.toByte() // con_end
        data[4] = 16.toByte()   // result (success)
        data[5] = logCount.toByte() // log count

        // Fill remaining data
        for (i in 6 until totalSize - 1) {
            data[i] = 0x00.toByte()
        }

        data[totalSize - 1] = DiaconnG8Packet.getCRC(data, totalSize - 1)
        return data
    }

    private fun createPacketWithResult(result: Int): ByteArray {
        val data = ByteArray(20)
        data[0] = 0xef.toByte()
        data[1] = 0xb2.toByte()
        data[2] = 0x01.toByte()
        data[3] = 0x00.toByte()
        data[4] = result.toByte()

        for (i in 5 until 19) {
            data[i] = 0xff.toByte()
        }

        data[19] = DiaconnG8Packet.getCRC(data, 19)
        return data
    }

    /**
     * Creates a packet with a single log entry
     * @param logKind The LOG_KIND byte identifying the log type
     * @param timestamp 4-byte timestamp (little-endian)
     * @param bolusAmount Bolus amount in 0.01U (e.g., 5000 = 50.00 U)
     * @param duration Duration for extended bolus (in 10-minute units)
     * @param tbRate Temp basal rate (50000 + percent, or 1000 + absolute*100)
     * @param tbDuration Temp basal duration (in 15-minute units)
     * @param reason Reason code for failures/stops
     * @param pattern Basal pattern number
     * @param remainAmount Remaining insulin amount
     * @param primeAmount Prime amount
     * @param batteryLevel Battery level
     * @param blockAmount Block alarm amount
     * @param remainInsulin Remaining insulin for shortage alarm
     * @param mealAmount Meal bolus amount for daily log
     * @param extAmount Extended bolus amount for daily log
     * @param basalAmount Basal amount for daily basal log
     * @param beforeAmount Before amount for hourly basal
     * @param afterAmount After amount for hourly basal
     */
    private fun createPacketWithLog(
        logKind: Byte,
        timestamp: Int,
        bolusAmount: Int = 0,
        duration: Int = 0,
        tbRate: Int = 0,
        tbDuration: Int = 0,
        reason: Int = 0,
        pattern: Int = 0,
        remainAmount: Int = 0,
        primeAmount: Int = 0,
        batteryLevel: Int = 0,
        blockAmount: Int = 0,
        remainInsulin: Int = 0,
        mealAmount: Int = 0,
        extAmount: Int = 0,
        basalAmount: Int = 0,
        beforeAmount: Int = 0,
        afterAmount: Int = 0
    ): ByteArray {
        val data = ByteArray(20)

        // Header
        data[0] = 0xef.toByte() // SOP
        data[1] = 0xb2.toByte() // msgType
        data[2] = 0x01.toByte() // seq
        data[3] = 0x00.toByte() // con_end
        data[4] = 16.toByte()   // result (success)
        data[5] = 1.toByte()    // log count = 1

        // Log entry (15 bytes)
        var pos = 6

        // Wrapping count (1 byte)
        data[pos++] = 0.toByte()

        // Log number (2 bytes, little-endian)
        data[pos++] = (1000 and 0xFF).toByte()
        data[pos++] = ((1000 shr 8) and 0xFF).toByte()

        // Log data (12 bytes)
        // Bytes 0-3: timestamp (little-endian)
        data[pos++] = (timestamp and 0xFF).toByte()
        data[pos++] = ((timestamp shr 8) and 0xFF).toByte()
        data[pos++] = ((timestamp shr 16) and 0xFF).toByte()
        data[pos++] = ((timestamp shr 24) and 0xFF).toByte()

        // Byte 4: typeAndKind (logKind is in lower 6 bits)
        data[pos++] = (logKind and 0x3F).toByte()

        // Bytes 5-11: vary by log type - fill with relevant data
        when (logKind) {
            0x08.toByte(), 0x09.toByte(), 0x0A.toByte(), 0x0B.toByte() -> {
                // Bolus logs: setAmount (2), injectAmount (2), injectTime (1), time (1), battery (1)
                data[pos++] = (bolusAmount and 0xFF).toByte()
                data[pos++] = ((bolusAmount shr 8) and 0xFF).toByte()
                data[pos++] = (bolusAmount and 0xFF).toByte()
                data[pos++] = ((bolusAmount shr 8) and 0xFF).toByte()
                data[pos++] = duration.toByte()
                data[pos++] = 1.toByte() // meal time
                data[pos++] = batteryLevel.toByte()
            }
            0x0C.toByte() -> {
                // LogSetSquareInjection: setAmount (2), normAmount (2), squareTime (1), time (1), battery (1)
                data[pos++] = (bolusAmount and 0xFF).toByte()
                data[pos++] = ((bolusAmount shr 8) and 0xFF).toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = duration.toByte()
                data[pos++] = 1.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x0D.toByte() -> {
                // LogInjectSquareSuccess: injectAmount (2), injectTime (1), time (1), battery (1)
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = duration.toByte()
                data[pos++] = 1.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x0E.toByte() -> {
                // LogInjectSquareFail: injectAmount (2), injectTime (1), reason (1), time (1), battery (1)
                data[pos++] = (bolusAmount and 0xFF).toByte()
                data[pos++] = ((bolusAmount shr 8) and 0xFF).toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = duration.toByte()
                data[pos++] = reason.toByte()
                data[pos++] = 1.toByte()
            }
            0x12.toByte(), 0x13.toByte() -> {
                // Temp basal logs: tbTime (1), tbInjectRateRatio (2), reason (1), battery (1)
                data[pos++] = tbDuration.toByte()
                data[pos++] = (tbRate and 0xFF).toByte()
                data[pos++] = ((tbRate shr 8) and 0xFF).toByte()
                data[pos++] = 0.toByte()
                data[pos++] = reason.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x03.toByte(), 0x04.toByte() -> {
                // Suspend logs: pattern (1)
                data[pos++] = pattern.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x1A.toByte(), 0x18.toByte(), 0x1C.toByte() -> {
                // Change logs: remainAmount (2), primeAmount (2), battery (1)
                data[pos++] = (remainAmount and 0xFF).toByte()
                data[pos++] = ((remainAmount shr 8) and 0xFF).toByte()
                data[pos++] = (primeAmount and 0xFF).toByte()
                data[pos++] = ((primeAmount shr 8) and 0xFF).toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x28.toByte() -> {
                // LogAlarmBattery: battery (1)
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x29.toByte() -> {
                // LogAlarmBlock: amount (2), reason (1), battery (1)
                data[pos++] = (blockAmount and 0xFF).toByte()
                data[pos++] = ((blockAmount shr 8) and 0xFF).toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = reason.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x2A.toByte() -> {
                // LogAlarmShortAge: remain (1), battery (1)
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = remainInsulin.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x01.toByte() -> {
                // LogResetSysV3: reason (1), battery (1)
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = reason.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x2F.toByte() -> {
                // LogInjection1Day: mealAmount (2), extAmount (2), battery (1)
                data[pos++] = (mealAmount and 0xFF).toByte()
                data[pos++] = ((mealAmount shr 8) and 0xFF).toByte()
                data[pos++] = (extAmount and 0xFF).toByte()
                data[pos++] = ((extAmount shr 8) and 0xFF).toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x2E.toByte() -> {
                // LogInjection1DayBasal: amount (2), battery (1)
                data[pos++] = (basalAmount and 0xFF).toByte()
                data[pos++] = ((basalAmount shr 8) and 0xFF).toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            0x2C.toByte() -> {
                // LogInjection1HourBasal: beforeAmount (2), afterAmount (2), battery (1)
                data[pos++] = (beforeAmount and 0xFF).toByte()
                data[pos++] = ((beforeAmount shr 8) and 0xFF).toByte()
                data[pos++] = (afterAmount and 0xFF).toByte()
                data[pos++] = ((afterAmount shr 8) and 0xFF).toByte()
                data[pos++] = 0.toByte()
                data[pos++] = 0.toByte()
                data[pos++] = batteryLevel.toByte()
            }
            else -> {
                // Unknown log type - fill with zeros
                for (i in 0 until 7) {
                    data[pos++] = 0.toByte()
                }
            }
        }

        // CRC
        data[19] = DiaconnG8Packet.getCRC(data, 19)
        return data
    }

    /**
     * Creates a timestamp representing a date in 2024
     * @return 4-byte timestamp value (seconds since epoch)
     */
    private fun createTimestamp(): Int {
        // Use a fixed timestamp for consistent testing (e.g., 2024-01-15 12:00:00)
        return 1705320000 // 2024-01-15 12:00:00 UTC
    }
}
