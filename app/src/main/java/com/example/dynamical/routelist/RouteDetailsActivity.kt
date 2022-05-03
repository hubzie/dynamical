package com.example.dynamical.routelist

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.MapFragment
import com.example.dynamical.R
import com.example.dynamical.data.Route
import com.example.dynamical.data.RouteViewModel
import com.example.dynamical.data.RouteViewModelFactory
import com.example.dynamical.databinding.RouteDetailsActivityBinding
import com.example.dynamical.mesure.Tracker.Companion.distanceToString
import com.example.dynamical.mesure.Tracker.Companion.timeToString
import kotlinx.coroutines.launch

class RouteDetailsActivity : AppCompatActivity() {
    private lateinit var binding: RouteDetailsActivityBinding
    private lateinit var factory: RouteDetailsItemFactory

    private lateinit var mapFragment: MapFragment

    private val routeViewModel: RouteViewModel by viewModels {
        RouteViewModelFactory((application as DynamicalApplication).repository)
    }

    private fun setup(route: Route) {
        mapFragment = MapFragment(false) {
            route.track?.let { track ->
                for (part in track)
                    mapFragment.newPolyline().points = part
                mapFragment.fitZoom()
            }
        }

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.map_fragment_container, mapFragment)
            commit()
        }

        route.time.let { binding.dataList.addView(
                factory.produce(getString(R.string.time_label, timeToString(it)))
        ) }
        route.stepCount?.let { binding.dataList.addView(
            factory.produce(getString(R.string.step_count_label, it.toString()))
        ) }
        route.distance?.let { binding.dataList.addView(
            factory.produce(getString(R.string.distance_label, distanceToString(it)))
        ) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RouteDetailsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = RouteDetailsItemFactory(this)

        setTitle(R.string.route_details_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra(getString(R.string.EXTRA_ROUTE_ID), -1)
        lifecycleScope.launch { setup(routeViewModel.getRouteDetails(id)) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}