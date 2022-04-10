package com.example.dynamical.routelist

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.example.dynamical.data.Route
import com.example.dynamical.data.RouteViewModel
import com.example.dynamical.data.RouteViewModelFactory
import com.example.dynamical.databinding.RouteDetailsActivityBinding
import kotlinx.coroutines.launch

class RouteDetailsActivity : AppCompatActivity() {
    private lateinit var binding: RouteDetailsActivityBinding
    private lateinit var route: Route

    private val routeViewModel: RouteViewModel by viewModels {
        RouteViewModelFactory((application as DynamicalApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RouteDetailsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra(getString(R.string.EXTRA_ROUTE_ID), -1)
        lifecycleScope.launch { route = routeViewModel.getRouteDetails(id) }
    }
}