package com.nightscout.eversense

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.SharedPreferences
import android.os.ParcelUuid
import android.util.Log
import com.nightscout.eversense.callbacks.EversenseScanCallback
import com.nightscout.eversense.enums.StorageKeys

class EversenseCGMPlugin {
    private var context: Context? = null

    private var bluetoothManager: BluetoothManager? = null
    private var preferences: SharedPreferences? = null

    private var scanner: EversenseScanner? = null
    private val gattCallback = EversenseGattCallback()

    fun setContext(context: Context) {
        this.context = context

        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
    }

    fun startScan(callback: EversenseScanCallback) {
        val bluetoothScanner = this.bluetoothManager?.adapter?.bluetoothLeScanner ?:run {
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

    fun connect(device: BluetoothDevice?): Boolean {
        val bluetoothManager = this.bluetoothManager ?:run {
            Log.e(TAG, "No bluetooth manager available. Make sure setContext has been called")
            return false
        }

        if (scanner != null) {
            bluetoothManager.adapter.bluetoothLeScanner.stopScan(scanner)
        }

        if (device != null) {
            Log.i(TAG, "Connecting to ${device.name}")
            device.connectGatt(context, true, gattCallback)
            return true
        }

        val address = preferences?.getString(StorageKeys.REMOTE_DEVICE_KEY, null) ?:run {
            Log.e(TAG, "Remote device not stored. Make sure you've connected once and bonded to this device")
            return false
        }

        val remoteDevice = bluetoothManager.adapter.getRemoteDevice(address) ?:run {
            Log.e(TAG, "Remote device not found. Make sure you've connected once and bonded to this device")
            return false
        }
        remoteDevice.connectGatt(context, true, gattCallback)
        return true
    }

    companion object {
        private val TAG = "EversenseCGMManager"
        val instance:EversenseCGMPlugin by lazy {
            EversenseCGMPlugin()
        }
    }
}