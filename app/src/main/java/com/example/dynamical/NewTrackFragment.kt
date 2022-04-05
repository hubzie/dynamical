package com.example.dynamical

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dynamical.databinding.NewTrackFragmentBinding

class NewTrackFragment : Fragment(R.layout.new_track_fragment) {
    private var _binding: NewTrackFragmentBinding? = null
    private val binding get() = _binding!!
    private var _sensorHandler: SensorHandler? = null
    private val sensorHandler get() = _sensorHandler!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewTrackFragmentBinding.inflate(layoutInflater, container, false)
        _sensorHandler = SensorHandler(requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager)

        sensorHandler.setOnChangeAction { binding.stepCountTextView.text = "$it" }
        sensorHandler.start()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensorHandler.stop()
        _sensorHandler = null
        _binding = null
    }
}