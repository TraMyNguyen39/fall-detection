package com.example.falldetection.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.falldetection.MyApplication
import com.example.falldetection.R
import com.example.falldetection.databinding.FragmentDetailBinding
import com.example.falldetection.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DetailFragment : Fragment(), MenuProvider, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentDetailBinding
    private lateinit var progressBar: ProgressBar

    private val args: DetailFragmentArgs by navArgs()
    private lateinit var mAdapter: FallHistoryAdapter
    private val viewModel: DeviceHistoryViewModel by activityViewModels {
        val deviceRepository =
            (requireActivity().application as MyApplication).deviceRepository
        val userRepository =
            (requireActivity().application as MyApplication).userRepository
        val userDeviceRepository =
            (requireActivity().application as MyApplication).userDeviceRepository

        DeviceHistoryViewModelFactory(deviceRepository, userRepository, userDeviceRepository)
    }
    private lateinit var reminderName: String
    private lateinit var patientEmail: String
    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_refresh)
        progressBar.visibility = View.VISIBLE

        reminderName = args.reminderName
        patientEmail = args.patientEmail
        userEmail = Utils.getCurrentEmail(requireContext())
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
        viewModel.loadDeviceInfo(patientEmail)
        binding.swipeDetailLayout.isRefreshing = false
    }

    private fun setUpObserver() {
        viewModel.device.observe(viewLifecycleOwner) { deviceUser ->
            val fullName = deviceUser?.fullName ?: ""
            val birthDay = Utils.dateToString(deviceUser?.birthDate) ?: "Không rõ"

            binding.textDetailFullName.text = fullName
            binding.textDetailReminderName.text = reminderName
            binding.textDetailBirthDate.text = birthDay
            binding.textDetailEmail.text = patientEmail
            if (deviceUser?.avtUrl != null) {
                val fileName = deviceUser.avtUrl as String
                val bucketUrl = "gs://falling-detection-3e200.appspot.com/avatars/"

                val storage: FirebaseStorage = FirebaseStorage.getInstance()
                val storageRef: StorageReference = storage.getReferenceFromUrl(bucketUrl)
                val imageRef: StorageReference = storageRef.child(fileName)
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(binding.imageAvt).load(uri).error(R.drawable.avatar_1_raster)
                        .into(binding.imageAvt)
                }.addOnFailureListener { exception ->
                    // Xử lý lỗi nếu có
                    Glide.with(binding.imageAvt).load(R.drawable.avatar_1_raster)
                        .error(R.drawable.avatar_1_raster)
                        .into(binding.imageAvt)
                    Log.e("FirebaseStorage", "Failed to get download URL", exception)
                }
            } else {
                Glide.with(binding.imageAvt)
                    .load(R.drawable.avatar_1_raster)
                    .error(R.drawable.avatar_1_raster).into(binding.imageAvt)
            }

            binding.btnDeleteObserver.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Hủy theo dõi")
                builder.setMessage("Bạn có chắc chắn muốn hủy theo dõi \"${reminderName}\" không?")
                builder.setPositiveButton("Có") { dialog, _ ->
                    viewModel.deleteObserver(userEmail!!, patientEmail) {
                        if (it) {
                            Snackbar.make(
                                requireView(), "Hủy theo dõi thành công", Snackbar.LENGTH_LONG
                            ).show()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        } else {
                            Snackbar.make(
                                requireView(), R.string.txt_unknown_error, Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                    dialog.cancel()
                }
                builder.setNegativeButton("Không") { dialog, _ ->
                    dialog.cancel()
                }
                builder.show()
            }

            if (deviceUser != null) {
                mAdapter.updateReminderName(reminderName)
                viewModel.loadFallHistory(patientEmail)
            }
            progressBar.visibility = View.GONE
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
            binding.swipeDetailLayout.isRefreshing = false
        }
    }
}