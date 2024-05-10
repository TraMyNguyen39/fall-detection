package com.example.falldetection.ui.forget_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.falldetection.MyApplication
import com.example.falldetection.R
import com.example.falldetection.databinding.FragmentForgetPasswordBinding
import com.example.falldetection.utils.Utils
import com.example.falldetection.utils.Utils.afterTextChanged
import com.google.android.material.snackbar.Snackbar

class ForgetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgetPasswordBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var navController: NavController

    // Check if form is correct formatted
    private var isCorrectForm: Boolean = false

    private val viewModel: ForgetPasswordViewModel by activityViewModels {
        val userRepository = (requireActivity().application as MyApplication).userRepository
        ForgetPasswordViewModelFactory(userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onResume() {
        super.onResume()
        isCorrectForm = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_authen)

        setupObservers()
        setupActions()
        return binding.root
    }
    override fun onPause() {
        super.onPause()
        binding.editForgetPassEmail.text?.clear()
    }
    private fun setupObservers() {
        viewModel.forgetPassFormState.observe(viewLifecycleOwner) {
            if (it != null) {
                isCorrectForm = false
                binding.editForgetPassEmail.error = getString(it)
            } else {
                isCorrectForm = true
            }
        }

        viewModel.forgetPassMessage.observe(viewLifecycleOwner) {
            progressBar.visibility = View.GONE
            if (it != null) {
                Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_LONG).show()
                if (it == R.string.txt_check_your_email_forget_pass) {
                    viewModel.resetMessage()
                    navController.navigate(ForgetPasswordFragmentDirections.actionGlobalLoginFragment())
                }
            }
        }
    }

    private fun setupActions() {
        binding.btnFpassBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.btnFpassConfirmEmail.setOnClickListener {
            view?.let { view -> Utils.hideKeyBoard(view) }

            if (isCorrectForm) {
                val email = binding.editForgetPassEmail.text.toString()
                progressBar.visibility = View.GONE
                forgetPassword(email)
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.txt_require_enter_info),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.editForgetPassEmail.afterTextChanged {
            val email = binding.editForgetPassEmail.text.toString()
            viewModel.forgetPasswordFormChange(email)
        }

        binding.editForgetPassEmail.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus) {
                val email = binding.editForgetPassEmail.text.toString()
                viewModel.forgetPasswordFormChange(email)
//            }
        }
    }

    private fun forgetPassword(email: String) {
        progressBar.visibility = View.VISIBLE
        view?.let { view -> Utils.hideKeyBoard(view) }
        viewModel.sendResetPassword(email)
    }
}