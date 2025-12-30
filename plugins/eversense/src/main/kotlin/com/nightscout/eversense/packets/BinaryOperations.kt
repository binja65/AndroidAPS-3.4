package com.nightscout.eversense.packets

class BinaryOperations {
    companion object {
        fun generateChecksumCRC16(data: ByteArray): ByteArray {
            var crc = 0xFFFF

            for (byte in data) {
                var currentByte = byte.toInt() and 0xFF
                repeat(8) {
                    val xor = ((crc shr 15) and 0x01) xor ((currentByte shr 7) and 0x01)
                    crc = (crc shl 1) and 0xFFFF
                    if (xor != 0) {
                        crc = (crc xor 0x1021) and 0xFFFF
                    }
                    currentByte = (currentByte shl 1) and 0xFF
                }
            }

            return byteArrayOf(
                crc.toByte(),
                (crc shr 8).toByte()
            )
        }
    }
}