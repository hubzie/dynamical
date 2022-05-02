package com.example.dynamical

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
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

    // TODO: make color dependent from theme
    private var markerIcon: BitmapDescriptor? = null
    private var marker: Marker? = null

    private val polylineList: MutableList<Polyline> = mutableListOf()

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

    private fun updatePosition() {
        if (!::map.isInitialized) return
        // map.moveCamera(CameraUpdateFactory.newLatLng(position))

        marker?.remove()
        marker = if (position == null) null
        else map.addMarker(
            MarkerOptions()
                .position(position!!)
                .icon(markerIcon)
                .anchor(0.5f, 0.5f)
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        updatePosition()
    }

    fun reset() {
        marker?.remove()
        polylineList.forEach { it.remove() }
        polylineList.clear()
    }

    // TODO: make color dependent from theme
    fun newPolyline(): Polyline {
        val polyline = map.addPolyline(PolylineOptions()).apply {
            startCap = RoundCap()
            endCap = RoundCap()
            jointType = JointType.ROUND
            width = resources.getDimension(R.dimen.line_width)
            /* val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(
                androidx.appcompat.R.attr.colorPrimary,
                typedValue,
                true
            )
            color = typedValue.data */
            color = ResourcesCompat.getColor(resources, R.color.purple_500, null)
        }

        polylineList.add(polyline)
        return polyline
    }
}