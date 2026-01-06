package com.nightscout.eversense.packets

import com.nightscout.eversense.EversenseLogger
import com.nightscout.eversense.enums.EversenseSecurityType
import com.nightscout.eversense.packets.e3.EversenseE3Packets
import com.nightscout.eversense.packets.e3.util.EversenseE3Writer

abstract class EversenseBasePacket : Object() {
    abstract fun getRequestData(): ByteArray
    abstract fun parseResponse(): Response?

    protected var receivedData = UByteArray(0)

    fun getAnnotation(): EversensePacket? {
        return this.javaClass.annotations.find { it.annotationClass == EversensePacket::class } as? EversensePacket
    }

    protected fun getStartIndex(): Int {
        val annotation = getAnnotation() ?:run {
            EversenseLogger.error("EversenseBasePacket", this.javaClass.name + " does not have the EversensePacket annotation...")
            return 0
        }

        return when(annotation.responseId) {
            EversenseE3Packets.ReadSingleByteSerialFlashRegisterResponseId,
            EversenseE3Packets.ReadTwoByteSerialFlashRegisterResponseId,
            EversenseE3Packets.ReadFourByteSerialFlashRegisterResponseId -> 4

            else -> 1
        }
    }

    fun appendData(data: UByteArray) {
        receivedData += data
    }

    fun buildRequest(): ByteArray? {
        val annotation = getAnnotation() ?:run {
            EversenseLogger.error("EversenseBasePacket", this.javaClass.name + " does not have the EversensePacket annotation...")
            return null
        }

        if (annotation.securityType == EversenseSecurityType.None) {
            var requestData = byteArrayOf(annotation.requestId)
            requestData += this.getRequestData()
            requestData += EversenseE3Writer.generateChecksumCRC16(requestData)

            return requestData
        } else {
            EversenseLogger.error("EversenseBasePacket", "TODO: Implement Eversense 365 request builder...")
            return null
        }
    }

    abstract class Response {}
}