package com.example.dynamical.routelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.data.RouteViewModel
import com.example.dynamical.data.RouteViewModelFactory
import com.example.dynamical.databinding.RouteListFragmentBinding

class RouteListFragment : Fragment() {
    private var _binding: RouteListFragmentBinding? = null
    private val binding get() = _binding!!

    private val routeViewModel: RouteViewModel by viewModels {
        RouteViewModelFactory((requireActivity().application as DynamicalApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RouteListFragmentBinding.inflate(inflater, container, false)

        val routeAdapter = RouteAdapter()
        binding.routeListView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = routeAdapter
            addItemDecoration(GridSpacingDecoration(10, 10))
        }

        routeViewModel.allRoutesOnline.observe(requireActivity()) { data ->
            data?.let { routeAdapter.submitList(data) }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}