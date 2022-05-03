package com.example.dynamical.newtrack.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.MapFragment
import com.example.dynamical.R
import com.example.dynamical.databinding.NewTrackFragmentBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline

class NewTrackFragment : Fragment(R.layout.new_track_fragment), NewTrackView {
    // Binding
    private var _binding: NewTrackFragmentBinding? = null
    private val binding get() = _binding!!

    // NewTrackPresenter
    private var _presenter: NewTrackPresenter? = null
    private val presenter get() = _presenter!!

    // Map fragment
    private var _mapFragment: MapFragment? = null
    private val mapFragment get() = _mapFragment!!

    // Interface implementation
    override val lifecycleOwner: LifecycleOwner = this


    override var locationPermission: Boolean = false
        private set
    private var requestCallback: (() -> Unit)? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            locationPermission = it
            requestCallback?.invoke()
        }

    override fun requestPermission(callback: () -> Unit) {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        requestCallback = callback
        if (permission == PackageManager.PERMISSION_GRANTED) locationPermission = true
        else requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }


    override fun setTime(time: String) {
        binding.timeTextView.text = time
    }

    override fun setStepCount(stepCount: String) {
        binding.stepCountTextView.text = stepCount
    }

    override fun setLocation(location: Location) {
        mapFragment.position = LatLng(location.latitude, location.longitude)
    }

    override fun setDistance(distance: String) {
        binding.distanceTextView.text = distance
    }

    override fun getNewPolyline(): Polyline {
        return mapFragment.newPolyline()
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
        mapFragment.reset()
    }


    // Fragment interface implementation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Setup binding
        _binding = NewTrackFragmentBinding.inflate(layoutInflater, container, false)

        // Setup button
        binding.actionButton.setOnClickListener { presenter.onFlipState() }
        binding.resetButton.setOnClickListener { presenter.onReset() }

        _mapFragment = MapFragment()
        with(requireActivity().supportFragmentManager.beginTransaction()) {
            replace(R.id.map_fragment_container, mapFragment)
            commit()
        }

        // Setup presenter
        _presenter = NewTrackPresenter(this, requireActivity().application as DynamicalApplication)
        presenter.initialize()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _presenter = null
        _mapFragment = null
    }
}