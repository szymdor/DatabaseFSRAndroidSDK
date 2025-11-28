package com.szymdor.fsr

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

@SuppressLint("MissingPermission")
class ConnectGATTSample(private val context: Context) {

    companion object {
        // SIG Automation IO service and Analog Input characteristic (full 128-bit)
        val SERVICE_UUID: UUID = UUID.fromString("00001815-0000-1000-8000-00805f9b34fb")
        val CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A58-0000-1000-8000-00805f9b34fb")
    }

    private var bluetoothGatt: BluetoothGatt? = null

    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState

    private val _fsrReading = MutableStateFlow<Int?>(null)
    val fsrReading: StateFlow<Int?> = _fsrReading

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _connectionState.value = true
                // Discover services
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _connectionState.value = false
                _fsrReading.value = null
                close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(SERVICE_UUID)
                val char = service?.getCharacteristic(CHARACTERISTIC_UUID)
                if (char != null) {
                    // Enable notifications
                    val enabled = gatt.setCharacteristicNotification(char, true)
                    // Write descriptor to enable remote notifications
                    val descriptor = char.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                    descriptor?.let {
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(it)
                    }
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (characteristic.uuid == CHARACTERISTIC_UUID) {
                val data = characteristic.value ?: return
                // Expect little-endian unsigned short (2 bytes)
                if (data.size >= 2) {
                    val bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
                    val unsigned = bb.short.toInt() and 0xFFFF
                    _fsrReading.value = unsigned
                }
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS && characteristic.uuid == CHARACTERISTIC_UUID) {
                val data = characteristic.value ?: return
                if (data.size >= 2) {
                    val bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
                    val unsigned = bb.short.toInt() and 0xFFFF
                    _fsrReading.value = unsigned
                }
            }
        }
    }

    fun connect(device: BluetoothDevice) {
        close() // close any existing
        bluetoothGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            device.connectGatt(context, false, gattCallback)
        }
    }

    fun readCharacteristicOnce() {
        val gatt = bluetoothGatt ?: return
        val char = gatt.getService(SERVICE_UUID)?.getCharacteristic(CHARACTERISTIC_UUID) ?: return
        gatt.readCharacteristic(char)
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        close()
    }

    fun close() {
        try {
            bluetoothGatt?.close()
        } catch (_: Exception) { }
        bluetoothGatt = null
        _connectionState.value = false
    }
}