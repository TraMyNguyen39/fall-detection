package com.example.falldetection.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.falldetection.MyApplication
import com.example.falldetection.R
import com.example.falldetection.databinding.FragmentDetailBinding
import com.example.falldetection.utils.Utils
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment(), MenuProvider, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentDetailBinding
    private val args : DetailFragmentArgs by navArgs()
    private lateinit var mAdapter: FallHistoryAdapter
    private val viewModel: DeviceHistoryViewModel by activityViewModels {
        val repository =
            (requireActivity().application as MyApplication).deviceRepository
        DeviceHistoryViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        // Add menu provider
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // set up refresh swipe
        binding.swipeDetailLayout.setOnRefreshListener(this)
        // set up adapter
        setUpAdapter()
        // Set up View
        reloadDataView()
        setUpObserver()

        return binding.root
    }

    private fun setUpAdapter() {
        mAdapter = FallHistoryAdapter(arrayListOf())
        binding.rvFallHistory.adapter = mAdapter
    }

    private fun reloadDataView() {
        binding.swipeDetailLayout.isRefreshing = true
        val deviceUserId = args.deviceUserId // deviceUserId always non null
        viewModel.loadDeviceInfo(deviceUserId)
        binding.swipeDetailLayout.isRefreshing = false
    }

    private fun setUpObserver() {
        viewModel.device.observe(viewLifecycleOwner) { deviceUser ->
            val fullName = deviceUser?.fullName ?: ""
            val birthDay = Utils.dateToString(deviceUser?.birthDate) ?: "Không rõ"

            binding.textDetailFullName.text = getString(R.string.txt_full_name_example, fullName)
            binding.textDetailReminderName.text = deviceUser?.reminderName ?: ""
            binding.textDetailBirthDate.text = getString(R.string.text_birthday_example, birthDay)
            if (deviceUser != null) {
                mAdapter.updateReminderName(deviceUser.reminderName)
                viewModel.loadFallHistory(deviceUser.deviceId)
            }
        }

        viewModel.listFallHistory.observe(viewLifecycleOwner) {
            mAdapter.updateList(it)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//        menuInflater.inflate(R.menu.main_menu, menu)
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
//            androidx.appcompat.R.id.home -> {
////                        viewModel.updateDeviceDetail(null)
////                        requireActivity().onBackPressedDispatcher.onBackPressed()
//                true
//            }

            else -> false
        }
    }

    override fun onRefresh() {
        if (Utils.isOnline(requireContext())) {
            reloadDataView()
        } else {
            Snackbar.make(binding.swipeDetailLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                .show()
            binding.swipeDetailLayout.isRefreshing = false;
        }
    }
}