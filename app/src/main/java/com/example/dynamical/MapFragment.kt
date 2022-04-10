package com.example.dynamical

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dynamical.databinding.MapFragmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapFragment : Fragment(R.layout.map_fragment), OnMapReadyCallback {
    private var _binding: MapFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private var _position = LatLng(0.0, 0.0)
    var position: LatLng
        get() = _position
        set(value) {
            _position = value
            updatePosition()
        }

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
    }

    private fun updatePosition() {
        if (!::map.isInitialized) return
        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        updatePosition()
    }
}