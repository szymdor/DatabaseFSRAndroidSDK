package com.szymdor.fsr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SignalsViewModelFactory(private val dao: SignalDao): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignalsViewModel::class.java)){
            return SignalsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}