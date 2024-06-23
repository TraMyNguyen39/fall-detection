package com.example.falldetection.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.example.falldetection.MyApplication
import com.example.falldetection.R
import com.example.falldetection.data.model.User
import com.example.falldetection.databinding.FragmentProfileBinding
import com.example.falldetection.utils.Gender
import com.example.falldetection.utils.Role
import com.example.falldetection.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class ProfileFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var progressBar: ProgressBar
    private val viewModel: ProfileViewModel by activityViewModels {
        val repository = (requireActivity().application as MyApplication).userRepository
        ProfileViewModelFactory(repository)
    }
    private var userEmail: String? = null
    private var formChanged: Boolean = false
    private var isPatient = false

    private var fileName: String? = null
    private var uri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
//        if (isGranted) {
            pickImageFromGallery()
//        } else {
//            showMessage(R.string.gallery_denied, Snackbar.LENGTH_LONG)
//        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), this::handleResult
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_refresh)
        progressBar.visibility = View.VISIBLE

        userEmail = Utils.getCurrentEmail(requireContext())
        if (userEmail == null) requireActivity().onBackPressedDispatcher.onBackPressed()
        else viewModel.loadProfile(userEmail!!)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupObserver()
        setupActions()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.resetState()
        formChanged = false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.main_menu_item_logout -> {
                logout()
                true
            }

            else -> false
        }
    }

    private fun logout() {
        // Xóa token & xóa auth
        userEmail?.let { viewModel.signout(it) }
        // Xóa pref
        val sharedPref = requireContext().getSharedPreferences(
            getString(R.string.preference_account_key), Context.MODE_PRIVATE
        )
        sharedPref.edit().apply {
            clear()
            apply()
        }
        requireActivity().finish()
    }

    private fun setupObserver() {
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            showProfile(profile)
        }

        viewModel.profileFormState.observe(viewLifecycleOwner) {
            if (it.errorMessage != null) {
                Snackbar.make(
                    requireView(), it.errorMessage, Snackbar.LENGTH_LONG
                ).show()
            }
        }

        viewModel.updateOrRegisterPatientMess.observe(viewLifecycleOwner) {
            if (it != null) {
                Snackbar.make(
                    requireView(), it, Snackbar.LENGTH_LONG
                ).show()
            }

            if (it == R.string.txt_register_patient_success) {
                binding.btnProfileRegisterPatient.isEnabled = false
            } else if (it == R.string.txt_update_profile_success
                || it == R.string.txt_cancel_patient_success
            ) {
                viewModel.loadProfile(userEmail!!)
            }
        }

        viewModel.updateMessage.observe(viewLifecycleOwner) {
            progressBar.visibility = View.GONE
            if (it == R.string.txt_update_avatar_success) {
                if (uri != null) {
                    binding.ivProfileAvt.setImageURI(uri)
                }
            }
            if (it != null) {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupActions() {
        binding.btnProfileSaveChange.setOnClickListener {
            Utils.hideKeyBoard(requireView())
            if (formChanged) {
                val fullName = binding.editProfileFullName.text.toString()
                val birthdate = binding.editProfileBirthDate.text.toString()
                val phoneNumber = binding.editProfilePhone.text.toString()
                val gender = if (binding.radioBtnMale.isChecked) Gender.MALE else Gender.FEMALE

                val isFormCorrect = viewModel.checkFormState(fullName, birthdate, phoneNumber)
                if (isFormCorrect) {
                    viewModel.updateProfile(userEmail!!, fullName, birthdate, phoneNumber, gender)
                }
            } else {
                Snackbar.make(
                    requireView(), "Bạn chưa nhập thay đổi nào", Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.btnProfileRegisterPatient.setOnClickListener {
            Utils.hideKeyBoard(requireView())

            if (!isPatient) {
                val fullName = binding.editProfileFullName.text.toString()
                val birthdate = binding.editProfileBirthDate.text.toString()
                val phoneNumber = binding.editProfilePhone.text.toString()
                val gender = if (binding.radioBtnMale.isChecked) Gender.MALE else Gender.FEMALE

                val isFormCorrect = viewModel.checkFormState(fullName, birthdate, phoneNumber)
                if (isFormCorrect) {
                    viewModel.registerBringDevice(
                        userEmail!!,
                        fullName,
                        birthdate,
                        phoneNumber,
                        gender
                    )
                }
            } else {
//                Snackbar.make(requireView(), "Hủy mang thiết bị", Snackbar.LENGTH_LONG)
//                    .setAction("Bạn có chắc chắn hủy mang thiết bị?", )

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Hủy mang thiết bị")
                builder.setMessage("Bạn có chắc chắn hủy mang thiết bị?")
                builder.setPositiveButton("OK") { dialog, _ ->
                    viewModel.cancelBringDevice(userEmail!!)
                    dialog.cancel()
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                builder.show()

            }
        }
//
        binding.btnChangeAvt.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickImageFromGallery()
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    showMessage(R.string.gallery_permission_prompt, Snackbar.LENGTH_LONG, true)
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }

        binding.editProfileFullName.doAfterTextChanged {
            formChanged = true
        }
        binding.editProfileBirthDate.doAfterTextChanged {
            formChanged = true
        }
        binding.editProfilePhone.doAfterTextChanged {
            formChanged = true
        }
        binding.radioBtnMale.setOnCheckedChangeListener { _, _ ->
            formChanged = true
        }
        binding.radioBtnFemale.setOnCheckedChangeListener { _, _ ->
            formChanged = true
        }
    }

    private fun handleResult(activityResult: ActivityResult?) {
        if (activityResult?.resultCode == Activity.RESULT_OK) {
            uri = activityResult.data?.data
            if (uri != null) {
                progressBar.visibility = View.VISIBLE
                fileName = getFileName(uri!!)
                // Lưu lên Firebase Storage
                val storageRef = Firebase.storage.reference
                val uploadTask = storageRef.child("avatars/${fileName}").putFile(uri!!)
                uploadTask.addOnSuccessListener {
                    // Tiếp tục cập nhật địa chỉ avt vào firestore
                    viewModel.updateAvatar(userEmail!!, fileName!!)
                    binding.ivProfileAvt.setImageURI(uri)
                }.addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Snackbar.make(requireView(), R.string.txt_unknown_error, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }
        return fileName
    }
    private fun pickImageFromGallery() {
        // Intent để chọn ảnh từ gallery
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        cameraLauncher.launch(intent)
    }

    private fun showMessage(messageId: Int, duration: Int, showAction: Boolean = false) {
        val snackBar = Snackbar.make(binding.root, messageId, duration)
        if (showAction && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            snackBar.setAction("OK") {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            snackBar.setAction("No thank") {
                showMessage(R.string.camera_permission_denied, Snackbar.LENGTH_LONG)
            }
        }
        snackBar.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showProfile(user: User?) {
        user?.let {
            binding.textProfileFullName.text = user.fullName
            binding.editProfileFullName.setText(user.fullName)
            binding.editProfileEmail.setText(userEmail)
            binding.editProfileBirthDate.setText(Utils.dateToString(user.birthDate))
            binding.editProfilePhone.setText(user.phoneNumber)
            if (user.gender == Gender.MALE.ordinal) {
                binding.radioBtnMale.isChecked = true
            } else {
                binding.radioBtnFemale.isChecked = true
            }
//            Glide.with(binding.ivProfileAvt).load(user.avtUrl).error(R.drawable.avatar_1)
//                .into(binding.ivProfileAvt)
            if (user.role == Role.DEVICE.ordinal) {
                binding.btnProfileRegisterPatient.text = getString(R.string.txt_cancel_bring_device)
                binding.btnProfileRegisterPatient.setBackgroundColor(requireActivity().getColor(R.color.stroke))
                isPatient = true
            } else {
                binding.btnProfileRegisterPatient.text = getString(R.string.txt_register_patient)
                binding.btnProfileRegisterPatient.setBackgroundColor(requireActivity().getColor(R.color.light_red))

                isPatient = false
            }

            if (user.avtUrl != null) {
                val fileName = user.avtUrl as String
                val bucketUrl = "gs://falling-detection-3e200.appspot.com/avatars/"

                val storage: FirebaseStorage = FirebaseStorage.getInstance()
                val storageRef: StorageReference = storage.getReferenceFromUrl(bucketUrl)
                val imageRef: StorageReference = storageRef.child(fileName)
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(binding.ivProfileAvt).load(uri).error(R.drawable.avatar_1_raster)
                        .into(binding.ivProfileAvt)
                }.addOnFailureListener { exception ->
                    // Xử lý lỗi nếu có
                    Glide.with(binding.ivProfileAvt).load(R.drawable.avatar_1_raster)
                        .error(R.drawable.avatar_1_raster)
                        .into(binding.ivProfileAvt)
                    Log.e("FirebaseStorage", "Failed to get download URL", exception)
                }
            } else {
                Glide.with(binding.ivProfileAvt)
                    .load(R.drawable.avatar_1_raster)
                    .error(R.drawable.avatar_1_raster).into(binding.ivProfileAvt)
            }

            formChanged = false
        }
        progressBar.visibility = View.GONE
    }
}