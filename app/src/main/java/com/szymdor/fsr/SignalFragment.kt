package com.szymdor.fsr

import android.content.Context
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.szymdor.fsr.databinding.FragmentSignalBinding
import androidx.navigation.fragment.findNavController

import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry

import androidx.navigation.fragment.navArgs

class SignalFragment : Fragment() {
    private var _binding: FragmentSignalBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SignalsViewModel

    private val args: SignalFragmentArgs by navArgs()

    private lateinit var chart: ScatterChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(this.activity).application
        val dao = SignalDatabase.getInstance(application).signalDao
        val viewModelFactory = SignalsViewModelFactory(dao)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SignalsViewModel::class.java)

        _binding = FragmentSignalBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner

        //button Add 2 action
        binding.click2.setOnClickListener {
            viewModel.newValue = 2.0F
            viewModel.addSignal()
        }

        //button Add 5 action
        binding.click5.setOnClickListener {
            viewModel.newValue = 5.0F
            viewModel.addSignal()
        }

        //Uwzględnić progi alarmowe: args.alarmLow i args.alarmHigh
        chart = binding.plot
        setupChart()

        viewModel.signals?.observe(viewLifecycleOwner) { signals ->
            updateChart(signals)
        }

        return binding.root
    }

    private fun setupChart() {
        chart.apply {
            description.isEnabled = false

            //Enable user interactions
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            isDoubleTapToZoomEnabled = true
            isHighlightPerTapEnabled = true

            //Axis configuration
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            xAxis.granularity = 1f
            axisLeft.granularity = 1f

            //Animation
            animateX(800)
            animateY(800)

            //Zoom & drag limits
            //Prevent zooming out too far (minScaleX/Y = 1f = normal)
            //and zooming in too far (maxScaleX/Y = 5f = 5× zoom)
            viewPortHandler.setMinimumScaleX(1f)
            viewPortHandler.setMaximumScaleX(5f)
            viewPortHandler.setMinimumScaleY(1f)
            viewPortHandler.setMaximumScaleY(5f)

            // Optional: limit dragging beyond chart bounds
            setDragOffsetX(10f)
            setDragOffsetY(10f)
        }
    }

    private fun updateChart(signals: List<Signal>) {

        val entries = signals.mapIndexed { index, signal ->
            Entry(index.toFloat(), signal.signalValue)
        }

        val dataSet = ScatterDataSet(entries, "Signal").apply {
            color = Color.BLUE
            valueTextSize = 10f
            setScatterShape(ScatterChart.ScatterShape.CIRCLE)
            scatterShapeSize = 8f
            setDrawValues(false)
            highLightColor = Color.RED
        }

        val scatterData = ScatterData(dataSet)
        chart.data = scatterData
        chart.invalidate() // Refresh chart
    }

    fun navigateToConfigFragment() {
        binding.viewModel?.clearAllSignals()
        findNavController().navigate(R.id.action_signal_to_config)
    }

    private fun checkLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}