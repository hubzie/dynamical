package com.example.dynamical.newtrack.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.MapFragment
import com.example.dynamical.R
import com.example.dynamical.databinding.NewTrackFragmentBinding
import com.google.android.gms.maps.model.LatLng

class NewTrackFragment : Fragment(R.layout.new_track_fragment), NewTrackView {
    // Binding
    private var _binding: NewTrackFragmentBinding? = null
    private val binding get() = _binding!!

    // NewTrackPresenter
    private var _presenter: NewTrackPresenter? = null
    private val presenter get() = _presenter!!

    // Map fragment
    private val mapFragment = MapFragment()

    // Interface implementation
    override val lifecycleOwner: LifecycleOwner = this


    // TODO: wait for activity result before reading
    override var locationPermission: Boolean = false
        private set

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            locationPermission = it
        }

    override fun requestPermission() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

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

    override fun drawRoute(points: List<LatLng>) {
        mapFragment.updateRoute(points)
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

        mapFragment.position = LatLng(50.049683, 19.944544)
        with(requireActivity().supportFragmentManager.beginTransaction()) {
            replace(R.id.map_fragment_container, mapFragment)
            commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _presenter = null
        _binding = null
    }
}