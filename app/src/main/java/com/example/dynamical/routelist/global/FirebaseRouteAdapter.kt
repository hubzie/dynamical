package com.example.dynamical.routelist.global

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.dynamical.R
import com.example.dynamical.firebase.GlobalRoute
import com.example.dynamical.firebase.GlobalRoute.Companion.toRoute
import com.example.dynamical.routelist.view.RouteViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class FirebaseRouteAdapter(options: FirestoreRecyclerOptions<GlobalRoute>) :
    FirestoreRecyclerAdapter<GlobalRoute, RouteViewHolder>(options)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.route_list_item, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int, model: GlobalRoute) {
        holder.bind(toRoute(model, snapshots.getSnapshot(position).id), true)
    }

    override fun onViewRecycled(holder: RouteViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }
}