package com.example.falldetection.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.falldetection.MyApplication
import com.example.falldetection.R
import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.databinding.FragmentHomeBinding
import com.example.falldetection.viewmodel.UserDevicesViewModel
import com.example.falldetection.viewmodel.UserDevicesViewModelFactory
import com.example.falldetection.viewmodel.UserViewModel
import com.example.falldetection.viewmodel.UserViewModelFactory

class HomeFragment : Fragment(), MenuProvider, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mAdapter: HomeAdapter
    private val viewModel: UserDevicesViewModel by activityViewModels {
        val repository =
            (requireActivity().application as MyApplication).userDeviceRepository
        UserDevicesViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Add menu provider
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // set up refresh swipe
        binding.swipeLayout.setOnRefreshListener(this);
        // set up adapter & observer
        setUpAdapter()
        setObserver()

        Log.d("TAG", R.drawable.avatar_2.toString())
        Log.d("TAG", R.drawable.avatar_3.toString())
        return binding.root

    }

    private fun setUpAdapter() {
        mAdapter = HomeAdapter(arrayListOf())
        binding.recyclerUsers.adapter = mAdapter
//        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
//        binding.recyclerUsers.addItemDecoration(divider)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObserver() {
        viewModel.listDevices.observe(viewLifecycleOwner) {
            mAdapter.updateList(it)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchView = menu.findItem(R.id.main_menu_item_search).actionView as SearchView
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.main_menu_item_profile -> {

                true
            }

            else -> false
        }
    }

    override fun onRefresh() {
        viewModel.loadUserDevices()
        binding.swipeLayout.isRefreshing = false;
    }

}