package com.example.dynamical.routelist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.data.DatabaseViewModel
import com.example.dynamical.data.DatabaseViewModelFactory
import com.example.dynamical.databinding.RouteListFragmentBinding
import com.example.dynamical.firebase.GlobalRoute
import com.example.dynamical.routelist.global.FirebaseRouteAdapter
import com.example.dynamical.routelist.local.RoomRouteAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class RouteListFragment : Fragment() {
    private var _binding: RouteListFragmentBinding? = null
    private val binding get() = _binding!!

    private val databaseViewModel: DatabaseViewModel by viewModels {
        DatabaseViewModelFactory((requireActivity().application as DynamicalApplication).repository)
    }

    private val roomRouteAdapter = RoomRouteAdapter()
    private val firebaseRouteAdapter: FirebaseRouteAdapter

    init {
        val query = FirebaseFirestore.getInstance().collection("route_table")
        val options = FirestoreRecyclerOptions.Builder<GlobalRoute>()
            .setQuery(query, GlobalRoute::class.java)
            .build()
        firebaseRouteAdapter = FirebaseRouteAdapter(options)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RouteListFragmentBinding.inflate(inflater, container, false)

        binding.routeListView.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(GridSpacingDecoration(10, 10))
        }

        setRoomRouteAdapter()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) setRoomRouteAdapter()
                else setFirebaseRouteAdapter()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return binding.root
    }

    private fun setRoomRouteAdapter() {
        binding.routeListView.adapter = roomRouteAdapter

        firebaseRouteAdapter.stopListening()
        databaseViewModel.allRoutesOnline.observe(viewLifecycleOwner) { data ->
            data?.let { roomRouteAdapter.submitList(data) }
        }
    }

    private fun setFirebaseRouteAdapter() {
        binding.routeListView.adapter = firebaseRouteAdapter

        firebaseRouteAdapter.startListening()
        databaseViewModel.allRoutesOnline.removeObservers(viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}