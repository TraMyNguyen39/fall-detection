package com.example.falldetection.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
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

            if (it == R.string.txt_update_profile_success
                || it == R.string.txt_cancel_patient_success
            ) {
                viewModel.loadProfile(userEmail!!)
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
            Glide.with(binding.ivProfileAvt).load(user.avtUrl).error(R.drawable.avatar_1)
                .into(binding.ivProfileAvt)
            if (user.role == Role.DEVICE.ordinal) {
                binding.btnProfileRegisterPatient.text = getString(R.string.txt_cancel_bring_device)
                binding.btnProfileRegisterPatient.setBackgroundColor(requireActivity().getColor(R.color.stroke))
                isPatient = true
            } else {
                binding.btnProfileRegisterPatient.text = getString(R.string.txt_register_patient)
                binding.btnProfileRegisterPatient.setBackgroundColor(requireActivity().getColor(R.color.light_red))

                isPatient = false
            }
            formChanged = false
        }
        progressBar.visibility = View.GONE
    }
}