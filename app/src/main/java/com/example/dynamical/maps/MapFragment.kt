package com.example.dynamical.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.example.dynamical.R
import com.example.dynamical.databinding.MapFragmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapFragment(private val doTrackPosition: Boolean, private val onReadyCallback: (() -> Unit)?) :
    Fragment(R.layout.map_fragment),
    OnMapReadyCallback
{
    private var _binding: MapFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap

    private var markerIcon: BitmapDescriptor? = null
    private var marker: Marker? = null

    private val polylineList: MutableList<Polyline> = mutableListOf()

    private var followPosition = true
    var position: LatLng? = null
        set(value) {
            field = value
            updatePosition()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MapFragmentBinding.inflate(inflater, container, false)
        markerIcon = BitmapDescriptorFactory.fromBitmap(
            ResourcesCompat
                .getDrawable(resources, R.drawable.position_dot, null)
            !!.toBitmap()
        )

        val mapView = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapView.getMapAsync(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Configure interactions
        if(doTrackPosition) {
            binding.locationButton.setOnClickListener {
                followPosition = true
                updatePosition()
                binding.locationButton.visibility = View.GONE
            }
            map.setOnCameraMoveStartedListener { reason ->
                if (reason == REASON_GESTURE) {
                    followPosition = false
                    binding.locationButton.visibility = View.VISIBLE
                }
            }
        }

        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        updatePosition()
        onReadyCallback?.invoke()
    }

    private fun updatePosition() {
        if (!::map.isInitialized) return

        if (followPosition)
            position?.let { map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 14.0f)) }

        marker?.remove()
        marker = if (position == null) null
        else map.addMarker(
            MarkerOptions()
                .position(position!!)
                .icon(markerIcon)
                .anchor(0.5f, 0.5f)
        )
    }

    fun reset() {
        marker?.remove()
        polylineList.forEach { it.remove() }
        polylineList.clear()
    }

    fun fitZoom() {
        val boundsBuilder = LatLngBounds.Builder()
        var count = 0
        for (polyline in polylineList)
            for (point in polyline.points) {
                count++
                boundsBuilder.include(point)
            }

        if (count > 0) {
            val padding =
                (resources.getDimension(R.dimen.map_zoom_padding) / resources.displayMetrics.density).toInt()
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), padding))
        }
    }

    fun newPolyline(): Polyline {
        val polyline = PolylineFactory.createPolyline(map)
        polylineList.add(polyline)
        return polyline
    }
}