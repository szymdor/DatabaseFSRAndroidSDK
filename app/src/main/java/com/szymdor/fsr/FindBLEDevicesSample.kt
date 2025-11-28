package com.szymdor.fsr

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class FindBLEDevicesSample : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BLEDeviceScreen(this)
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BLEDeviceScreen(context: Context) {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter

    var devices by remember { mutableStateOf(listOf<BluetoothDevice>()) }

    // Permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            bluetoothAdapter.bondedDevices?.let { bonded ->
                devices = bonded.toList()
            }
        }
    }

    // Check and request permission
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            bluetoothAdapter.bondedDevices?.let { bonded ->
                devices = bonded.toList()
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("BLE Devices") }) }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(devices) { device ->
                DeviceItem(device)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* handle click */ }
            .padding(16.dp)
    ) {
        Text(text = device.name ?: "Unknown Device", style = MaterialTheme.typography.h6)
        Text(text = device.address, style = MaterialTheme.typography.body2)
    }
}