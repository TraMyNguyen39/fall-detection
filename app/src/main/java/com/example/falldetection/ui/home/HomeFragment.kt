package com.example.falldetection.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.falldetection.MyApplication
import com.example.falldetection.R
import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.databinding.FragmentHomeBinding
import com.example.falldetection.utils.Utils
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(), MenuProvider, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mAdapter: HomeAdapter
    private val viewModel: UserDevicesViewModel by activityViewModels {
        val repository =
            (requireActivity().application as MyApplication).userDeviceRepository
        TrackedDevicesViewModelFactory(repository)
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
        binding.swipeLayout.setOnRefreshListener(this)

        // set up adapter & observer
        setUpAdapter()
        setObserver()
        return binding.root

    }

    private fun setUpAdapter() {
        mAdapter = HomeAdapter(arrayListOf(), object : HomeAdapter.OnClickListener {
            override fun onClick(device: UserDevice) {
                val deviceUserId = device.id
                val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(deviceUserId)
                findNavController().navigate(action)
            }

        })

        val layoutManager =
            GridLayoutManager(
                requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.recyclerUsers.adapter = mAdapter
        binding.recyclerUsers.layoutManager = layoutManager
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObserver() {
        viewModel.listDevices.observe(viewLifecycleOwner) {
            mAdapter.updateList(it)
        }

//        viewModel.device.observe(viewLifecycleOwner) {
//            if (it != null) {
//                val deviceUserId = it.id
//                val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(deviceUserId)
//                findNavController().navigate(action)
//            }
//        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchView = menu.findItem(R.id.main_menu_item_search).actionView as SearchView
        // Thiết lập màu nền cho vùng nhập liệu
//        searchView.setBackgroundColor(Color.WHITE)

        // Thiết lập màu của biểu tượng tìm kiếmandroidx.appcompat.R.id.search_mag_icon
//        val searchIcon = searchView.findViewById(android.R.id) as ImageView
//        searchIcon.setColorFilter(Color.WHITE)

//        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
//        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
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
        if (Utils.isOnline(requireContext())) {
            viewModel.loadUserDevices()
        } else {
            Snackbar.make(binding.swipeLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                .show()
        }
        binding.swipeLayout.isRefreshing = false
    }
}