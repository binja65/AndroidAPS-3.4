package com.nightscout.eversense.packets

import android.util.Log
import com.nightscout.eversense.enums.EversenseSecurityType
import com.nightscout.eversense.packets.e3.EversenseE3Packets

abstract class EversenseBasePacket : Object() {
    abstract fun getRequestData(): ByteArray
    abstract fun parseResponse(): Response?

    protected var receivedData = ByteArray(0)

    fun getAnnotation(): EversensePacket? {
        return this.javaClass.annotations.find { it.annotationClass == EversensePacket::class } as? EversensePacket
    }

    protected fun getStartIndex(): Int {
        val annotation = getAnnotation() ?:run {
            Log.e("EversenseBasePacket", this.javaClass.name + " does not have the EversensePacket annotation...")
            return 0
        }

        return when(annotation.responseId) {
            EversenseE3Packets.ReadSingleByteSerialFlashRegisterResponseId,
            EversenseE3Packets.ReadTwoByteSerialFlashRegisterResponseId,
            EversenseE3Packets.ReadFourByteSerialFlashRegisterResponseId -> 3

            else -> 0
        }
    }

    fun appendData(data: ByteArray) {
        receivedData += data
    }

    fun buildRequest(): ByteArray? {
        val annotation = getAnnotation() ?:run {
            Log.e("EversenseBasePacket", this.javaClass.name + " does not have the EversensePacket annotation...")
            return null
        }

        if (annotation.securityType == EversenseSecurityType.None) {
            var requestData = byteArrayOf(annotation.requestId)
            requestData += this.getRequestData()
            requestData += BinaryOperations.generateChecksumCRC16(requestData)

            return requestData
        } else {
            Log.e("EversenseBasePacket", "TODO: Implement Eversense 365 request builder...")
            return null
        }
    }

    abstract class Response {}
}