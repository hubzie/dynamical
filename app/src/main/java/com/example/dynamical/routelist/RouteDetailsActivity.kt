package com.example.dynamical.routelist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.example.dynamical.data.DatabaseViewModel
import com.example.dynamical.data.DatabaseViewModelFactory
import com.example.dynamical.data.Route
import com.example.dynamical.databinding.RouteDetailsActivityBinding
import com.example.dynamical.maps.MapFragment
import com.example.dynamical.maps.PolylineType
import com.example.dynamical.mesure.Tracker.Companion.distanceToString
import com.example.dynamical.mesure.Tracker.Companion.timeToString
import kotlinx.coroutines.launch

class RouteDetailsActivity : AppCompatActivity() {
    private lateinit var binding: RouteDetailsActivityBinding
    private lateinit var factory: RouteDetailsItemFactory

    private lateinit var mapFragment: MapFragment

    private lateinit var route: Route

    lateinit var menu: Menu

    private val databaseViewModel: DatabaseViewModel by viewModels {
        DatabaseViewModelFactory((application as DynamicalApplication).repository)
    }

    private fun setup(route: Route) {
        this.route = route

        mapFragment = MapFragment(false) {
            route.track?.let { track ->
                for (part in track)
                    mapFragment.newPolyline(PolylineType.CURRENT).points = part
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
        lifecycleScope.launch { setup(databaseViewModel.getRouteDetails(id)) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.route_details_menu, menu)
        this.menu = menu

        val followedRoute = (application as DynamicalApplication).followedRoute

        if(followedRoute == null || followedRoute != route.id)
            menu.findItem(R.id.unfollow_route).isVisible = false
        else
            menu.findItem(R.id.follow_route).isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.delete_route -> {
                databaseViewModel.deleteRoute(route)
                finish()
                true
            }
            R.id.follow_route -> {
                (application as DynamicalApplication).followedRoute = route.id
                menu.findItem(R.id.follow_route).isVisible = false
                menu.findItem(R.id.unfollow_route).isVisible = true
                true
            }
            R.id.unfollow_route -> {
                (application as DynamicalApplication).followedRoute = null
                menu.findItem(R.id.follow_route).isVisible = true
                menu.findItem(R.id.unfollow_route).isVisible = false
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}