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
import com.example.dynamical.routelist.local.RoomRouteAdapter
import com.google.android.material.tabs.TabLayout

class RouteListFragment : Fragment() {
    private var _binding: RouteListFragmentBinding? = null
    private val binding get() = _binding!!

    private val databaseViewModel: DatabaseViewModel by viewModels {
        DatabaseViewModelFactory((requireActivity().application as DynamicalApplication).repository)
    }

    private val roomRouteAdapter = RoomRouteAdapter()

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
                setRoomRouteAdapter()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return binding.root
    }

    private fun setRoomRouteAdapter() {
        binding.routeListView.adapter = roomRouteAdapter

        databaseViewModel.allRoutesOnline.observe(viewLifecycleOwner) { data ->
            data?.let { roomRouteAdapter.submitList(data) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
