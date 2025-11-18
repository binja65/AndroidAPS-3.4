package app.aaps.pump.diaconn.pumplog

import app.aaps.shared.tests.TestBase
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class PumpLogUtilTest : TestBase() {

    @Test
    fun hexStringToByteArrayConversion() {
        // Given
        val hexString = "48656C6C6F" // "Hello" in hex

        // When
        val result = PumpLogUtil.hexStringToByteArray(hexString)

        // Then
        assertThat(result.size).isEqualTo(5)
        assertThat(result[0]).isEqualTo(0x48.toByte()) // 'H'
        assertThat(result[1]).isEqualTo(0x65.toByte()) // 'e'
        assertThat(result[2]).isEqualTo(0x6C.toByte()) // 'l'
        assertThat(result[3]).isEqualTo(0x6C.toByte()) // 'l'
        assertThat(result[4]).isEqualTo(0x6F.toByte()) // 'o'
    }

    @Test
    fun getTypeExtractsUpperTwoBits() {
        // Given
        val testByte = 0xC5.toByte() // Binary: 11000101

        // When
        val type = PumpLogUtil.getType(testByte)

        // Then
        assertThat(type.toInt()).isEqualTo(3) // Upper 2 bits: 11 = 3
    }

    @Test
    fun getKindExtractsLowerSixBits() {
        // Given
        val testByte = 0xC5.toByte() // Binary: 11000101

        // When
        val kind = PumpLogUtil.getKind(testByte)

        // Then
        assertThat(kind.toInt()).isEqualTo(5) // Lower 6 bits: 000101 = 5
    }

    @Test
    fun getTypeFromDataString() {
        // Given - data with typeAndKind at position 4
        val data = "0000000042" // typeAndKind = 0x42 = 01000010

        // When
        val type = PumpLogUtil.getType(data)

        // Then
        assertThat(type.toInt()).isEqualTo(1) // Upper 2 bits: 01 = 1
    }

    @Test
    fun getKindFromDataString() {
        // Given - data with typeAndKind at position 4
        val data = "000000000A" // typeAndKind = 0x0A = 00001010

        // When
        val kind = PumpLogUtil.getKind(data)

        // Then
        assertThat(kind.toInt()).isEqualTo(10) // Lower 6 bits: 001010 = 10
    }

    @Test
    fun isPumpVersionGeComparesVersionsCorrectly() {
        // Test major version greater
        assertThat(PumpLogUtil.isPumpVersionGe("4.0", 3, 0)).isTrue()

        // Test major version less
        assertThat(PumpLogUtil.isPumpVersionGe("2.5", 3, 0)).isFalse()

        // Test same major, minor greater
        assertThat(PumpLogUtil.isPumpVersionGe("3.5", 3, 2)).isTrue()

        // Test same major, minor equal
        assertThat(PumpLogUtil.isPumpVersionGe("3.42", 3, 42)).isTrue()

        // Test same major, minor less
        assertThat(PumpLogUtil.isPumpVersionGe("3.1", 3, 5)).isFalse()
    }

    @Test
    fun isPumpVersionGeHandlesVersionStringsWithNonDigits() {
        // Given - version string with letters/characters
        val version = "v3.42-beta"

        // When/Then
        assertThat(PumpLogUtil.isPumpVersionGe(version, 3, 42)).isTrue()
        assertThat(PumpLogUtil.isPumpVersionGe(version, 3, 50)).isFalse()
    }

    @Test
    fun getDttmFormatsTimestampCorrectly() {
        // Given - a known timestamp as hex string
        // Unix timestamp 1609459200 = 2021-01-01 00:00:00 UTC
        // In hex (little endian): 0x5FEF0000 -> 00 00 EF 5F
        val data = "0000EF5F"

        // When
        val result = PumpLogUtil.getDttm(data)

        // Then
        assertThat(result).contains("2021-01-01")
    }

    @Test
    fun getTypeAndKindCombinations() {
        // Test various type and kind combinations
        val testCases = listOf(
            Triple(0x00.toByte(), 0, 0),     // type=00, kind=000000
            Triple(0x3F.toByte(), 0, 63),    // type=00, kind=111111
            Triple(0x40.toByte(), 1, 0),     // type=01, kind=000000
            Triple(0x80.toByte(), 2, 0),     // type=10, kind=000000
            Triple(0xC0.toByte(), 3, 0),     // type=11, kind=000000
            Triple(0xFF.toByte(), 3, 63)     // type=11, kind=111111
        )

        testCases.forEach { (byte, expectedType, expectedKind) ->
            assertThat(PumpLogUtil.getType(byte).toInt()).isEqualTo(expectedType)
            assertThat(PumpLogUtil.getKind(byte).toInt()).isEqualTo(expectedKind)
        }
    }
}
