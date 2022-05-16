package com.example.dynamical.routelist.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.dynamical.R

class RouteDetailsItemFactory(private val parent: ViewGroup) {
    fun produce(title: String, value: String): View {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.route_details_item, parent, false)

        (view.findViewById(R.id.item_description_title) as TextView).text = title
        (view.findViewById(R.id.item_description_value) as TextView).text = value

        return view
    }
}