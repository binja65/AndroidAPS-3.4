package app.aaps.pump.diaconn.packet

import app.aaps.core.interfaces.pump.DetailedBolusInfoStorage
import app.aaps.core.interfaces.pump.PumpSync
import app.aaps.core.interfaces.pump.TemporaryBasalStorage
import app.aaps.pump.diaconn.DiaconnG8Pump
import app.aaps.pump.diaconn.api.DiaconnLogUploader
import app.aaps.pump.diaconn.database.DiaconnHistoryRecordDao
import app.aaps.shared.tests.TestBaseWithProfile
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock

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
    fun handleMessageShouldProcessSingleLogEntry() {
        // Given - Valid response packet with 1 log entry
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createValidPacketWithLogs(1)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
    }

    @Test
    fun handleMessageShouldProcessMultipleLogEntries() {
        // Given - Valid response packet with 5 log entries
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createValidPacketWithLogs(5)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
    }

    @Test
    fun handleMessageShouldProcessMaximumLogEntries() {
        // Given - Valid response packet with 11 log entries (typical max per request)
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createValidPacketWithLogs(11)

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
    }

    @Test
    fun packetSizeShouldBeCorrectForVariableLogCount() {
        // Verify packet size calculation: 20 base + (15 * logCount)

        // 0 logs: 20 bytes
        val packet0 = createValidPacket(0)
        assertThat(packet0.size).isEqualTo(20)

        // 1 log: 20 + 15 = 35 bytes
        val packet1 = createValidPacket(1)
        assertThat(packet1.size).isEqualTo(35)

        // 5 logs: 20 + 75 = 95 bytes
        val packet5 = createValidPacket(5)
        assertThat(packet5.size).isEqualTo(95)

        // 11 logs: 20 + 165 = 185 bytes
        val packet11 = createValidPacket(11)
        assertThat(packet11.size).isEqualTo(185)
    }

    @Test
    fun handleMessageShouldHandleBolusLog() {
        // Given - Packet with a bolus log entry (type 0x08 = meal bolus success)
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithSpecificLog(
            wrappingCount = 0,
            logNum = 1234,
            logData = "23C1AB64088E128E127C0155" // Real meal bolus log data
        )

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
    }

    @Test
    fun handleMessageShouldHandleBasalLog() {
        // Given - Packet with a basal log entry (type 0x11 = 1-hour basal)
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithSpecificLog(
            wrappingCount = 0,
            logNum = 5678,
            logData = "23C1AB6411320064000000FF" // 1-hour basal log
        )

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
    }

    @Test
    fun handleMessageShouldHandleTempBasalLog() {
        // Given - Packet with temp basal log (type 0x18 = TB start)
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithSpecificLog(
            wrappingCount = 0,
            logNum = 9999,
            logData = "23C1AB6418C800780000C8FF" // TB start log
        )

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
    }

    @Test
    fun handleMessageShouldHandleWrappingCountCorrectly() {
        // Given - Packet with logs having different wrapping counts
        val packet = BigLogInquireResponsePacket(packetInjector)
        val data = createPacketWithMultipleLogs(
            listOf(
                LogEntry(wrappingCount = 0, logNum = 9998, logData = "23C1AB64080000000000FFFF"),
                LogEntry(wrappingCount = 0, logNum = 9999, logData = "23C1AB64110000000000FFFF"),
                LogEntry(wrappingCount = 1, logNum = 0, logData = "23C1AB64120000000000FFFF"), // Wrapped to next cycle
                LogEntry(wrappingCount = 1, logNum = 1, logData = "23C1AB64130000000000FFFF")
            )
        )

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
    }

    private data class LogEntry(
        val wrappingCount: Int,
        val logNum: Int,
        val logData: String // 12 bytes as hex string (24 characters)
    )

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

    private fun createValidPacketWithLogs(logCount: Int): ByteArray {
        // Create packet with realistic log data
        val logs = mutableListOf<LogEntry>()
        for (i in 0 until logCount) {
            logs.add(
                LogEntry(
                    wrappingCount = 0,
                    logNum = 1000 + i,
                    logData = "23C1AB64080000000000FFFF" // Generic log data
                )
            )
        }
        return createPacketWithMultipleLogs(logs)
    }

    private fun createPacketWithSpecificLog(wrappingCount: Int, logNum: Int, logData: String): ByteArray {
        return createPacketWithMultipleLogs(
            listOf(LogEntry(wrappingCount, logNum, logData))
        )
    }

    private fun createPacketWithMultipleLogs(logs: List<LogEntry>): ByteArray {
        val baseSize = 20
        val logSize = 15
        val totalSize = baseSize + (logs.size * logSize)
        val data = ByteArray(totalSize)

        // Header
        data[0] = 0xef.toByte() // SOP
        data[1] = 0xb2.toByte() // msgType
        data[2] = 0x01.toByte() // seq
        data[3] = 0x00.toByte() // con_end
        data[4] = 16.toByte()   // result (success)
        data[5] = logs.size.toByte() // log count

        var pos = 6
        // Add each log entry
        for (log in logs) {
            // Wrapping count (1 byte)
            data[pos++] = log.wrappingCount.toByte()

            // Log number (2 bytes) - LITTLE_ENDIAN
            data[pos++] = log.logNum.toByte()
            data[pos++] = (log.logNum shr 8).toByte()

            // Log data (12 bytes)
            val logBytes = hexStringToByteArray(log.logData)
            require(logBytes.size == 12) { "Log data must be exactly 12 bytes (24 hex chars)" }
            for (byte in logBytes) {
                data[pos++] = byte
            }
        }

        // CRC
        data[totalSize - 1] = DiaconnG8Packet.getCRC(data, totalSize - 1)
        return data
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((s[i].digitToInt(16) shl 4) + s[i + 1].digitToInt(16)).toByte()
            i += 2
        }
        return data
    }
}
