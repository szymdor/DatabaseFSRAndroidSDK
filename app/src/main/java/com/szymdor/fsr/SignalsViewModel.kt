package com.szymdor.fsr

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SignalsViewModel(val dao: SignalDao): ViewModel() {

    //assign read from microcontroller to new Value
    var newValue = 0.0F
    var lastValue: LiveData<Float>? = dao.getLastValue()
    var signals: LiveData<List<Signal>>? = dao.getLastValues()

    fun addSignal(){
        viewModelScope.launch {
            val signal = Signal()
            signal.signalValue = newValue
            dao.insert(signal)
        }
    }

    fun showLastSignals(){
        signals = dao.getLastValues()
        //way to show it on plot
    }

    fun clearAllSignals(){
        viewModelScope.launch {
            dao.deleteAllSignals()
        }
    }
}