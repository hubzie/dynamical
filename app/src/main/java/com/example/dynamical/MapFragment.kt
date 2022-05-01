package com.example.dynamical

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dynamical.databinding.MapFragmentBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapFragment : Fragment(R.layout.map_fragment), OnMapReadyCallback {
    private var _binding: MapFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private var _position = LatLng(0.0, 0.0)

    private var marker: Marker? = null

    var position: LatLng
        get() = _position
        set(value) {
            _position = value
            updatePosition()
        }

    private var _polyline: Polyline? = null
    private val polyline get() = _polyline!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MapFragmentBinding.inflate(inflater, container, false)

        val mapView = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapView.getMapAsync(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _polyline = null
    }

    private fun updatePosition() {
        if (!::map.isInitialized) return
        // map.moveCamera(CameraUpdateFactory.newLatLng(position))

        marker?.remove()
        marker = map.addMarker(MarkerOptions().position(position))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        _polyline = map.addPolyline(PolylineOptions())
        updatePosition()
    }

    fun updateRoute(points: List<LatLng>) {
        polyline.points = points
    }
}