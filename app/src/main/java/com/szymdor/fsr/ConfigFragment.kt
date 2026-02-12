package com.szymdor.fsr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.szymdor.fsr.databinding.FragmentConfigBinding
import androidx.navigation.fragment.findNavController

class ConfigFragment : Fragment() {

    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!

    //Czy to jest potrzebne???
    lateinit var viewModel: ConfigViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        binding.fragment = this
        return binding.root
    }

    fun navigateToSignalFragment() {
        val low = binding.lowValue.text.toString().toFloatOrNull()
        val high = binding.highValue.text.toString().toFloatOrNull()
        if (low != null && high != null) {
            if (low in 1.5F..high && high <= 5.0F) {

                val sourceOfData = binding.radioGroup.checkedRadioButtonId == R.id.bluetooth
                val action = ConfigFragmentDirections.actionConfigToSignal(isBT = sourceOfData, alarmLow = low, alarmHigh = high)
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, getString(R.string.komunikat_toast_1), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, getString(R.string.komunikat_toast_2), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}