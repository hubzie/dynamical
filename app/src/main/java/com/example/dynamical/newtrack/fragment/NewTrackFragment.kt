package com.example.dynamical.newtrack.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.example.dynamical.data.DatabaseViewModel
import com.example.dynamical.data.DatabaseViewModelFactory
import com.example.dynamical.databinding.NewTrackFragmentBinding
import com.example.dynamical.maps.MapFragment
import com.example.dynamical.maps.PolylineType
import com.example.dynamical.measure.Tracker
import com.example.dynamical.measure.TrackerViewModel
import com.example.dynamical.measure.TrackerViewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import kotlinx.coroutines.launch

class NewTrackFragment : Fragment(R.layout.new_track_fragment), NewTrackView {
    override val databaseViewModel: DatabaseViewModel by viewModels {
        DatabaseViewModelFactory((requireActivity().application as DynamicalApplication).repository)
    }
    private val trackerViewModel: TrackerViewModel by viewModels {
        TrackerViewModelFactory(Tracker.getTracker(requireActivity().application))
    }

    // Binding
    private var _binding: NewTrackFragmentBinding? = null
    private val binding get() = _binding!!

    // NewTrackPresenter
    private var _presenter: NewTrackPresenter? = null
    private val presenter get() = _presenter!!

    // Map fragment
    private var _mapFragment: MapFragment? = null
    private val mapFragment get() = _mapFragment!!

    // Followed track
    private var followedTrack: List<Polyline> = listOf()

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
        if (permission == PackageManager.PERMISSION_GRANTED) {
            locationPermission = true
            callback()
        } else requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }


    override fun setTime(time: String) {
        binding.timeTextView.text = time
        binding.timeInfo.visibility = View.VISIBLE
    }

    override fun setStepCount(stepCount: String) {
        binding.stepCountTextView.text = stepCount
        binding.stepCountInfo.visibility = View.VISIBLE
    }

    override fun setLocation(location: Location?) {
        mapFragment.position = location?.let { LatLng(it.latitude, it.longitude) }
    }

    override fun setDistance(distance: String) {
        binding.distanceTextView.text = distance
        binding.distanceInfo.visibility = View.VISIBLE
    }

    override fun getNewPolyline(type: PolylineType): Polyline {
        return mapFragment.newPolyline(type)
    }


    override fun hideStepCount() {
        binding.stepCountInfo.visibility = View.GONE
    }

    override fun hideDistance() {
        binding.distanceInfo.visibility = View.GONE
    }

    override fun hideTime() {
        binding.timeInfo.visibility = View.GONE
    }


    override fun onMeasureStart() {
        binding.actionButton.setImageResource(R.drawable.pause)
        binding.endButton.visibility = View.VISIBLE
    }

    override fun onMeasurePause() {
        binding.actionButton.setImageResource(R.drawable.start)
        binding.endButton.visibility = View.VISIBLE
    }

    override fun onMeasureReset() {
        binding.timeInfo.visibility = View.GONE
        binding.actionButton.setImageResource(R.drawable.start)
        binding.endButton.visibility = View.INVISIBLE
        mapFragment.reset()
        drawFollowedTrack()
    }


    private fun drawFollowedTrack() {
        lifecycleScope.launch {
            (requireActivity().application as DynamicalApplication).followedRoute?.let { route ->
                followedTrack = route.track?.map { part ->
                    getNewPolyline(PolylineType.FOLLOWED).apply { points = part }
                } ?: listOf()
            }
        }
    }


    // Fragment interface implementation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Setup binding
        _binding = NewTrackFragmentBinding.inflate(layoutInflater, container, false)

        // Setup visibility
        binding.timeInfo.visibility = View.GONE
        binding.distanceInfo.visibility = View.GONE
        binding.stepCountInfo.visibility = View.GONE
        if ((requireActivity().application as DynamicalApplication).followedRoute != null)
            binding.unfollowButton.visibility = View.VISIBLE

        // Setup button
        binding.actionButton.setOnClickListener { presenter.onFlipState() }
        binding.endButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.end_tracking_confirm_message)
                .setPositiveButton(R.string.delete_confirm_positive) { _, _ -> presenter.onEnd() }
                .setNegativeButton(R.string.delete_confirm_negative) { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        binding.unfollowButton.setOnClickListener {
            followedTrack.forEach { polyline -> polyline.remove() }
            it.visibility = View.INVISIBLE
            (requireActivity().application as DynamicalApplication).followedRoute = null
        }

        // Setup presenter and show followed track when the map become ready
        _mapFragment = MapFragment(true) {
            _presenter = NewTrackPresenter(
                this,
                requireActivity().application as DynamicalApplication,
                trackerViewModel
            )
            presenter.initialize()
            drawFollowedTrack()
        }

        with(requireActivity().supportFragmentManager.beginTransaction()) {
            replace(R.id.map_fragment_container, mapFragment)
            commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _presenter = null
        _mapFragment = null
    }
}