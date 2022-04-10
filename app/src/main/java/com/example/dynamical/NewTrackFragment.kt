package com.example.dynamical

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dynamical.databinding.NewTrackFragmentBinding
import com.google.android.gms.maps.model.LatLng

class NewTrackFragment : Fragment(R.layout.new_track_fragment) {
    private var _binding: NewTrackFragmentBinding? = null
    private val binding get() = _binding!!

    private var _sensorHandler: SensorHandler? = null
    private val sensorHandler get() = _sensorHandler!!

    private val stopwatch = Stopwatch()
    private var isRunning = false

    private fun start() {
        isRunning = true
        binding.actionButton.setImageResource(R.drawable.ic_baseline_pause_24)
        sensorHandler.start()
        stopwatch.start()
    }

    private fun stop() {
        isRunning = false
        binding.actionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        sensorHandler.stop()
        stopwatch.stop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewTrackFragmentBinding.inflate(layoutInflater, container, false)
        _sensorHandler =
            SensorHandler(requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager)
        stopwatch.reset()

        sensorHandler.setOnChangeAction { binding.stepCountTextView.text = "$it" }
        stopwatch.setOnTickAction { binding.timeTextView.text = Stopwatch.timeToString(it) }

        binding.actionButton.setOnClickListener {
            if (!isRunning) start()
            else stop()
        }

        val mapFragment = MapFragment()
        mapFragment.position = LatLng(50.049683, 19.944544)
        with(requireActivity().supportFragmentManager.beginTransaction()) {
            replace(R.id.map_fragment_container, mapFragment)
            commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stop()
        _sensorHandler = null
        _binding = null
    }
}