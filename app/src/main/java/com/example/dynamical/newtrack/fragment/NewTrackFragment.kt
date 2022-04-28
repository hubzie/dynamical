package com.example.dynamical.newtrack.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.example.dynamical.databinding.NewTrackFragmentBinding

class NewTrackFragment : Fragment(R.layout.new_track_fragment), NewTrackView {
    // Binding
    private var _binding: NewTrackFragmentBinding? = null
    private val binding get() = _binding!!

    // NewTrackPresenter
    private var _presenter: NewTrackPresenter? = null
    private val presenter get() = _presenter!!

    // Interface implementation
    override val lifecycleOwner: LifecycleOwner = this

    override fun setTime(time: String) {
        binding.timeTextView.text = time
    }
    override fun setStepCount(stepCount: String) {
        binding.stepCountTextView.text = stepCount
    }

    override fun onMeasureStart() {
        binding.actionButton.setImageResource(R.drawable.ic_baseline_pause_24)
        binding.resetButton.visibility = View.VISIBLE
    }
    override fun onMeasurePause() {
        binding.actionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        binding.resetButton.visibility = View.VISIBLE
    }
    override fun onMeasureReset() {
        binding.actionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        binding.resetButton.visibility = View.INVISIBLE
    }

    // Fragment interface implementation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Setup binding
        _binding = NewTrackFragmentBinding.inflate(layoutInflater, container, false)

        // Setup presenter
        _presenter = NewTrackPresenter(this, requireActivity().application as DynamicalApplication)
        presenter.initialize()

        // Setup button
        binding.actionButton.setOnClickListener { presenter.onFlipState() }
        binding.resetButton.setOnClickListener { presenter.onReset() }
/*
        val mapFragment = MapFragment()
        mapFragment.position = LatLng(50.049683, 19.944544)
        with(requireActivity().supportFragmentManager.beginTransaction()) {
            replace(R.id.map_fragment_container, mapFragment)
            commit()
        }
*/
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _presenter = null
        _binding = null
    }
}