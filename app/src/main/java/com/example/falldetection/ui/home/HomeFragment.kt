package com.example.falldetection.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import com.example.falldetection.ui.DangerousAlertActivity
import com.example.falldetection.utils.Role
import com.example.falldetection.utils.Utils
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(), MenuProvider, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mAdapter: HomeAdapter
    private lateinit var progressBar: ProgressBar
    private val viewModel: TrackedDevicesViewModel by activityViewModels {
        val repository =
            (requireActivity().application as MyApplication).userDeviceRepository
        val userRepository =
            (requireActivity().application as MyApplication).userRepository
        TrackedDevicesViewModelFactory(repository, userRepository)
    }
    private var userEmail : String? = null
    private var isAscending : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_refresh)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // get current account
        userEmail = Utils.getCurrentEmail(requireContext())

        if (userEmail == null)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        else {
            progressBar.visibility = View.VISIBLE
            viewModel.loadUserDevices(userEmail!!)
        }

        binding.swipeLayout.setOnRefreshListener(this)

        // set up adapter & observer
        setUpAdapter()
        setObserver()
        return binding.root

    }

    private fun setUpAdapter() {
        mAdapter = HomeAdapter(arrayListOf(), object : HomeAdapter.OnClickListener {
            override fun onClick(device: UserDevice) {
                val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                    patientEmail = device.patientEmail,
                    reminderName = device.reminderName
                )
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
            if (it.isEmpty()) {
                binding.emptyView.root.visibility = View.VISIBLE
                binding.recyclerUsers.visibility = View.GONE
            } else {
                binding.emptyView.root.visibility = View.GONE
                binding.recyclerUsers.visibility = View.VISIBLE
                mAdapter.updateList(it)
            }
            progressBar.visibility = View.GONE
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.main_menu_item_notification -> {
                val action = HomeFragmentDirections.actionHomeFragmentToRequestObserverFragment()
                findNavController().navigate(action)
                true
            }

            R.id.main_menu_item_sort -> {
                isAscending = !isAscending
                if (isAscending)
                    menuItem.setIcon(R.drawable.ic_sort)
                else
                    menuItem.setIcon(R.drawable.ic_sort_descending)

                mAdapter.sortList(isAscending)
                true
            }

            else -> false
        }
    }

    override fun onRefresh() {
        if (Utils.isOnline(requireContext())) {
            viewModel.loadUserDevices(userEmail!!)
        } else {
            Snackbar.make(binding.swipeLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                .show()
        }
        binding.swipeLayout.isRefreshing = false
    }
}