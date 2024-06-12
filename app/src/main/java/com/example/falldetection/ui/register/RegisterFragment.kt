package com.example.falldetection.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.falldetection.MyApplication
import com.example.falldetection.R
import com.example.falldetection.databinding.FragmentRegisterBinding
import com.example.falldetection.utils.Utils
import com.example.falldetection.utils.Utils.afterTextChanged
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var progressBar: ProgressBar
    // Check if form is correct formatted
    private var isCorrectForm: Boolean = false
    private var userEmail: String? = null

    private val viewModel: RegisterViewModel by activityViewModels {
        val observerRequestRepository =
            (requireActivity().application as MyApplication).observerRequestRepository
        val userDeviceRepository =
            (requireActivity().application as MyApplication).userDeviceRepository
        RegisterViewModelFactory(observerRequestRepository, userDeviceRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_refresh)
        progressBar.visibility = View.GONE

        userEmail = Utils.getCurrentEmail(requireContext())
        if (userEmail == null) requireActivity().onBackPressedDispatcher.onBackPressed()

        setupObservers()
        setupActions()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        isCorrectForm = false
    }

    override fun onPause() {
        super.onPause()
        clearForm()
    }

    private fun clearForm() {
        binding.editRegisterDeviceEmail.text?.clear()
        binding.editRegisterReminderName.text?.clear()
    }

    private fun setupActions() {
        binding.btnRegisterClear.setOnClickListener {
            view?.let { view -> Utils.hideKeyBoard(view) }
            clearForm()
        }

        binding.btnRegisterAdd.setOnClickListener {
            view?.let { view -> Utils.hideKeyBoard(view) }

            if (isCorrectForm) {
                progressBar.visibility = View.VISIBLE
                val patientEmail = binding.editRegisterDeviceEmail.text.toString()
                val reminderName = binding.editRegisterReminderName.text.toString()

                viewModel.registerObserver(userEmail!!, patientEmail, reminderName)
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.txt_require_enter_info),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.editRegisterDeviceEmail.afterTextChanged {
            val reminderName = binding.editRegisterReminderName.text.toString()
            viewModel.registerFormChange(it, reminderName)
        }

        binding.editRegisterReminderName.afterTextChanged {
            val email = binding.editRegisterDeviceEmail.text.toString()
            viewModel.registerFormChange(email, it)
        }
    }

    private fun setupObservers() {
        viewModel.registerFormState.observe(viewLifecycleOwner) {
            if (it.emailError != null) {
                isCorrectForm = false
                binding.editRegisterDeviceEmail.error = getString(it.emailError)
            } else if (it.reminderNameError != null) {
                isCorrectForm = false
                binding.editRegisterReminderName.error = getString(it.reminderNameError)
            } else {
                isCorrectForm = true
            }
        }

        viewModel.registerMessage.observe(viewLifecycleOwner) {
            progressBar.visibility = View.GONE
            if (it != null) {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(requireView(), R.string.txt_register_observer_success, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}