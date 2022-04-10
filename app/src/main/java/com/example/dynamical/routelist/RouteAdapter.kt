package com.example.dynamical.routelist

import android.content.Intent
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

class RouteAdapter : ListAdapter<Route, RouteAdapter.ViewHolder>(RouteDiff()) {
    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view),
        OnMapReadyCallback,
        View.OnClickListener
    {
        private val process: ProgressBar = view.findViewById(R.id.progress)
        private val mapView: MapView = view.findViewById(R.id.map_item_preview)
        private val text: TextView = view.findViewById(R.id.caption)
        private lateinit var map: GoogleMap
        private lateinit var position: LatLng
        private var id: Int = 0

        init {
            view.clipToOutline = true
            view.setOnClickListener(this)
            with(mapView) {
                onCreate(null)
                getMapAsync(this@ViewHolder)
                isClickable = false
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
            id = route.id

            "Distance: ${route.distance}m".also { text.text = it }
            position = route.position
            setMapLocation()
        }

        override fun onMapReady(googleMap: GoogleMap) {
            map = googleMap
            with(map.uiSettings) {
                isMapToolbarEnabled = false
                setAllGesturesEnabled(false)
            }

            setMapLocation()

            mapView.visibility = View.VISIBLE
            process.visibility = View.GONE
        }

        override fun onClick(view: View) {
            val intent = Intent(view.context, RouteDetailsActivity::class.java)
            intent.putExtra(view.context.getString(R.string.EXTRA_ROUTE_ID), id)
            view.context.startActivity(intent)
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
}