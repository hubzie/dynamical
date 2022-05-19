package com.example.dynamical.routelist.view

import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.example.dynamical.data.DatabaseViewModel
import com.example.dynamical.data.DatabaseViewModelFactory
import com.example.dynamical.data.Route
import com.example.dynamical.databinding.RouteDetailsActivityBinding
import com.example.dynamical.firebase.AnonymousSessionException
import com.example.dynamical.firebase.FirebaseDatabase
import com.example.dynamical.firebase.NetworkTimeoutException
import com.example.dynamical.maps.MapFragment
import com.example.dynamical.maps.PolylineType
import com.example.dynamical.measure.Tracker.Companion.distanceToString
import com.example.dynamical.measure.Tracker.Companion.timeToString

class RouteDetailsActivity : AppCompatActivity() {
    private lateinit var binding: RouteDetailsActivityBinding
    private lateinit var factory: RouteDetailsItemFactory

    private lateinit var mapFragment: MapFragment
    private lateinit var route: Route
    private lateinit var menu: Menu

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

        binding.dateLabel.text = DateFormat.getDateFormat(applicationContext)
            .format(route.date)
        route.time.let { binding.dataList.addView(
            factory.produce(getString(R.string.time_description_label), timeToString(it))
        ) }
        route.stepCount?.let { binding.dataList.addView(
            factory.produce(getString(R.string.step_count_description_label), it.toString())
        ) }
        route.distance?.let { binding.dataList.addView(
            factory.produce(getString(R.string.distance_description_label), distanceToString(it))
        ) }

        setupMenu()
    }

    private val progressDialog by lazy {
        AlertDialog.Builder(this)
            .setView(R.layout.progress_dialog)
            .setCancelable(false)
            .create()
    }

    private fun showLoading() {
        progressDialog.show()
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }

    private fun setupMenu() {
        if(!::menu.isInitialized || !::route.isInitialized) return

        val followedRoute = (application as DynamicalApplication).followedRoute
        if(followedRoute?.id != route.id)
            menu.findItem(R.id.unfollow_route).isVisible = false
        else
            menu.findItem(R.id.follow_route).isVisible = false

        if (route.globalId != null)
            menu.findItem(R.id.share_route).isVisible = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RouteDetailsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Displaying info
        factory = RouteDetailsItemFactory(binding.dataList)

        // Options bar
        setTitle(R.string.route_details_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val route: Route = intent.getParcelableExtra(getString(R.string.EXTRA_ROUTE))!!
        val isGlobal = intent.getBooleanExtra(
            getString(R.string.EXTRA_ROUTE_IS_GLOBAL),
            false
        )
        setup(route)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.route_details_menu, menu)

        this.menu = menu
        setupMenu()

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.delete_route -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.delete_confirm_message)
                    .setPositiveButton(R.string.delete_confirm_positive) { _, _ ->
                        if ((application as DynamicalApplication).followedRoute?.id == route.id)
                            (application as DynamicalApplication).followedRoute = null
                        databaseViewModel.deleteRoute(route)
                        finish()
                    }
                    .setNegativeButton(R.string.delete_confirm_negative) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()
                true
            }
            R.id.follow_route -> {
                (application as DynamicalApplication).followedRoute = route
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
            R.id.share_route -> {
                try {
                    showLoading()
                    FirebaseDatabase.shareRoute(route) { globalId, globalRoute ->
                        menu.findItem(R.id.share_route).isVisible = false

                        route.globalId = globalId
                        route.owner = globalRoute.owner
                        route.ownerName = globalRoute.ownerName

                        databaseViewModel.insertRoute(route)
                        hideLoading()
                        Toast.makeText(this, R.string.route_shared, Toast.LENGTH_LONG).show()
                    }
                } catch (e : Exception) {
                    val builder = AlertDialog.Builder(this)
                        .setNeutralButton(R.string.confirm_button) { dialog, _ ->
                            dialog.dismiss()
                        }

                    when(e) {
                        is NetworkTimeoutException ->
                            builder.setMessage(R.string.no_connection_error)
                        is AnonymousSessionException ->
                            builder.setMessage(R.string.no_user_error)
                        else ->
                            builder.setMessage(e.message)
                    }

                    builder.create().show()
                    hideLoading()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}