package com.szymdor.fsr

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object BluetoothSampleBox {

    // Initialize once from Application or Activity
    lateinit var appContext: Context
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val bluetoothManager: BluetoothManager
        get() = appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    val bluetoothAdapter: BluetoothAdapter?
        get() = bluetoothManager.adapter

    // App-wide CoroutineScope
    val appScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}