package com.szymdor.fsr
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel

class ConfigViewModel: ViewModel() {
    val min: Float = 1.5F
    val max: Float = 5.0F
}