package app.aaps.pump.diaconn.packet

import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.pump.diaconn.DiaconnG8Pump
import app.aaps.shared.tests.TestBaseWithProfile
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock

class BigAPSMainInfoInquireResponsePacketTest : TestBaseWithProfile() {

    @Mock lateinit var rh: ResourceHelper
    @Mock lateinit var preferences: Preferences

    private lateinit var diaconnG8Pump: DiaconnG8Pump

    private val packetInjector = HasAndroidInjector {
        AndroidInjector {
            if (it is BigAPSMainInfoInquireResponsePacket) {
                it.aapsLogger = aapsLogger
                it.dateUtil = dateUtil
                it.diaconnG8Pump = diaconnG8Pump
                it.preferences = preferences
                it.rh = rh
            }
        }
    }

    @BeforeEach
    fun setup() {
        diaconnG8Pump = DiaconnG8Pump(aapsLogger, dateUtil, decimalFormatter)
    }

    @Test
    fun handleMessageShouldProcessValidResponse() {
        // Given - Valid response packet with basic pump info
        val packet = BigAPSMainInfoInquireResponsePacket(packetInjector)
        val data = createValidPacket()

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isFalse()
        // Verify basic fields are parsed
        assertThat(diaconnG8Pump.systemRemainInsulin).isAtLeast(0.0)
        assertThat(diaconnG8Pump.systemRemainBattery).isAtLeast(0)
    }

    @Test
    fun handleMessageShouldFailOnDefectivePacket() {
        // Given
        val packet = BigAPSMainInfoInquireResponsePacket(packetInjector)
        val data = ByteArray(200)
        data[0] = 0x00 // Wrong SOP

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun handleMessageShouldFailOnInvalidResult() {
        // Given
        val packet = BigAPSMainInfoInquireResponsePacket(packetInjector)
        val data = createPacketWithResult(17) // Invalid result

        // When
        packet.handleMessage(data)

        // Then
        assertThat(packet.failed).isTrue()
    }

    @Test
    fun msgTypeShouldBeCorrect() {
        // Given
        val packet = BigAPSMainInfoInquireResponsePacket(packetInjector)

        // Then
        assertThat(packet.msgType).isEqualTo(0x94.toByte())
    }

    @Test
    fun friendlyNameShouldBeCorrect() {
        // Given
        val packet = BigAPSMainInfoInquireResponsePacket(packetInjector)

        // Then
        assertThat(packet.friendlyName).isEqualTo("PUMP_BIG_APS_MAIN_INFO_INQUIRE_RESPONSE")
    }

    private fun createValidPacket(): ByteArray {
        val data = ByteArray(200)
        data[0] = 0xef.toByte() // SOP
        data[1] = 0x94.toByte() // msgType
        data[2] = 0x01.toByte() // seq
        data[3] = 0x00.toByte() // con_end
        data[4] = 16.toByte()   // result (success)

        // Fill remaining data with valid values
        // Insulin remain (2 bytes) = 15000 (150.00 U)
        data[5] = 0x3A.toByte()
        data[6] = 0x98.toByte()

        // Battery remain (1 byte) = 80%
        data[7] = 80.toByte()

        // Base pattern (1 byte) = 1 (basic)
        data[8] = 1.toByte()

        // Fill rest with reasonable defaults
        for (i in 9 until 199) {
            data[i] = 0x00.toByte()
        }

        data[199] = DiaconnG8Packet.getCRC(data, 199)
        return data
    }

    private fun createPacketWithResult(result: Int): ByteArray {
        val data = ByteArray(200)
        data[0] = 0xef.toByte()
        data[1] = 0x94.toByte()
        data[2] = 0x01.toByte()
        data[3] = 0x00.toByte()
        data[4] = result.toByte()

        for (i in 5 until 199) {
            data[i] = 0xff.toByte()
        }

        data[199] = DiaconnG8Packet.getCRC(data, 199)
        return data
    }
}
