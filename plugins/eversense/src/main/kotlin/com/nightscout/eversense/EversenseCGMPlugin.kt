package com.nightscout.eversense

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import com.nightscout.eversense.callbacks.EversenseScanCallback
import com.nightscout.eversense.models.EversenseScanResult

class EversenseCGMPlugin {
    private var context: Context? = null
    private var bluetoothScanner: BluetoothLeScanner? = null

    private var scanner: EversenseScanner? = null
    private val gattCallback = EversenseGattCallback()

    fun setContext(context: Context) {
        this.context = context
        val bleManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothScanner = bleManager.adapter.bluetoothLeScanner
    }

    fun startScan(callback: EversenseScanCallback) {
        val bluetoothScanner = this.bluetoothScanner ?:run {
            Log.e(TAG, "No bluetooth manager available. Make sure setContext has been called")
            return
        }

        scanner = EversenseScanner(callback)
        val filters = listOf(
            ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(EversenseGattCallback.serviceUUID)).build()
        )
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        bluetoothScanner.startScan(filters, settings, scanner)
    }

    fun connect(device: BluetoothDevice?, name: String) {
        val bluetoothScanner = this.bluetoothScanner ?:run {
            Log.e(TAG, "No bluetooth manager available. Make sure setContext has been called")
            return
        }
        if (scanner != null) {
            bluetoothScanner.stopScan(scanner)
        }

        if (device != null) {
            Log.i(TAG, "Connecting to ${device.name}")
            device.connectGatt(context, true, gattCallback)
            return
        }

        // val bondedDevice = bluetoothManager.adapter.bondedDevices.find { it.name == name } ?:run {
        //     Log.e(TAG, "Could not find bond to the Eversense device. Please can for device first")
        //     return
        // }
        //
        // bondedDevice.connectGatt(context, true, gattCallback)
    }

    companion object {
        private val TAG = "EversenseCGMManager"
        val instance:EversenseCGMPlugin by lazy {
            EversenseCGMPlugin()
        }
    }
}