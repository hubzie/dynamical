package com.example.dynamical.routelist

import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.widget.TextView
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
import kotlinx.coroutines.launch

class RouteDetailsActivity : AppCompatActivity() {
    private lateinit var binding: RouteDetailsActivityBinding

    private val routeViewModel: RouteViewModel by viewModels {
        RouteViewModelFactory((application as DynamicalApplication).repository)
    }

    private fun setup(route: Route) {
        val mapFragment = MapFragment()
        mapFragment.position = route.position

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.map_fragment_container, mapFragment)
            commit()
        }

        val distanceInfo = TextView(this)
        with(distanceInfo) {
            text = "Distance: ${route.distance}"
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.value_size))
        }
        binding.dataList.addView(distanceInfo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RouteDetailsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

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