package com.example.dynamical.routelist

import androidx.recyclerview.widget.DiffUtil
import com.example.dynamical.data.Route

class RouteDiff : DiffUtil.ItemCallback<Route>() {
    override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem == newItem
    }
}