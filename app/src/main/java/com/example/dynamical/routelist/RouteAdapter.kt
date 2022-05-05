package com.example.dynamical.routelist

import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamical.R
import com.example.dynamical.data.Route
import com.example.dynamical.maps.PolylineFactory
import com.example.dynamical.mesure.Tracker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLngBounds

class RouteAdapter : ListAdapter<Route, RouteAdapter.ViewHolder>(RouteDiff()) {
    inner class ViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view),
        OnMapReadyCallback,
        View.OnClickListener {
        private val process: ProgressBar = view.findViewById(R.id.progress)
        private val mapView: MapView = view.findViewById(R.id.map_item_preview)
        private val description: LinearLayout = view.findViewById(R.id.item_description)

        private lateinit var map: GoogleMap

        private lateinit var route: Route

        init {
            view.clipToOutline = true
            view.setOnClickListener(this)
            with(mapView) {
                onCreate(null)
                getMapAsync(this@ViewHolder)
                isClickable = false
            }
        }

        private fun addInfo(value: String) {
            description.addView(TextView(description.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen.description_text_size)
                )
                text = value
            })
        }

        private fun initTrack() {
            if(!::map.isInitialized || !::route.isInitialized) return

            route.track?.let { track ->
                // Draw
                for (part in track)
                    PolylineFactory.createPolyline(map).points = part

                // Zoom
                val boundsBuilder = LatLngBounds.Builder()
                var count = 0
                for (polyline in track)
                    for (point in polyline) {
                        count++
                        boundsBuilder.include(point)
                    }

                if (count > 0) {
                    val mapBounds = boundsBuilder.build()

                    val cu = CameraUpdateFactory.newLatLngZoom(
                        mapBounds.center,
                        16f
                    )
                    map.moveCamera(cu)

                    while(true) {
                        val bounds = map.projection.visibleRegion.latLngBounds
                        if (bounds.contains(mapBounds.northeast) && bounds.contains(mapBounds.southwest))
                            break
                        else map.moveCamera(CameraUpdateFactory.zoomBy(-1f))
                    }
                }
            }

            mapView.visibility = View.VISIBLE
            process.visibility = View.GONE
        }

        fun bind(idx: Int) {
            route = getItem(idx)

            addInfo(view.context.getString(R.string.time_label, Tracker.timeToString(route.time)))
            route.distance?.let {
                addInfo(view.context.getString(R.string.distance_label, Tracker.distanceToString(it)))
            }

            initTrack()
        }

        override fun onMapReady(googleMap: GoogleMap) {
            map = googleMap
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            with(map.uiSettings) {
                isMapToolbarEnabled = false
                setAllGesturesEnabled(false)
            }

            map.setOnMapLoadedCallback { initTrack() }
        }

        override fun onClick(view: View) {
            val intent = Intent(view.context, RouteDetailsActivity::class.java)
            intent.putExtra(view.context.getString(R.string.EXTRA_ROUTE_ID), route.id)
            view.context.startActivity(intent)
        }

        fun clear() {
            map.clear()
            description.removeAllViews()
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

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }
}