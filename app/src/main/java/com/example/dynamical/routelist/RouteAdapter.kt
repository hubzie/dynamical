package com.example.dynamical.routelist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamical.R
import com.example.dynamical.data.Route
import com.example.dynamical.data.RouteDiff
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng

class RouteAdapter : ListAdapter<Route, RouteAdapter.ViewHolder>(RouteDiff()) {
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), OnMapReadyCallback {
        private val process: ProgressBar = view.findViewById(R.id.progress)
        private val mapView: MapView = view.findViewById(R.id.map_item_preview)
        private val text: TextView = view.findViewById(R.id.caption)
        private lateinit var map: GoogleMap
        private lateinit var position: LatLng

        init {
            view.clipToOutline = true
            with(mapView) {
                onCreate(null)
                getMapAsync(this@ViewHolder)
            }
        }

        private fun setMapLocation() {
            if (!::map.isInitialized) return
            with(map) {
                moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))
                mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }

        fun bind(idx: Int) {
            val route: Route = getItem(idx)
            "Distance: ${route.distance}m".also { text.text = it }
            position = route.position
            setMapLocation()
        }

        override fun onMapReady(googleMap: GoogleMap) {
            mapView.onCreate(null)

            MapsInitializer.initialize(context)
            map = googleMap
            map.uiSettings.isMapToolbarEnabled = false
            setMapLocation()

            mapView.visibility = View.VISIBLE
            process.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.route_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
}