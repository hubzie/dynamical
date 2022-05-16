package com.example.dynamical.routelist.local

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.dynamical.R
import com.example.dynamical.data.Route
import com.example.dynamical.routelist.view.RouteViewHolder

class RoomRouteAdapter : ListAdapter<Route, RouteViewHolder>(RouteDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.route_list_item, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: RouteViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }
}