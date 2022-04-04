package com.example.dynamical.routelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dynamical.R
import com.example.dynamical.databinding.RouteListFragmentBinding

class RouteListFragment : Fragment(R.layout.route_list_fragment) {
    private var _binding: RouteListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RouteListFragmentBinding.inflate(inflater, container, false)

        val list = binding.routeListView
        list.layoutManager = GridLayoutManager(context, 2)
        list.adapter = RouteAdapter()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}