package com.example.falldetection.ui.signup

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
import com.example.falldetection.databinding.FragmentSignupBinding
import com.example.falldetection.utils.Utils
import com.example.falldetection.utils.Utils.afterTextChanged
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var navController: NavController

    // Check if form is correct formatted
    private var isCorrectForm: Boolean = false

    private val viewModel: SignupViewModel by activityViewModels {
        val userRepository = (requireActivity().application as MyApplication).userRepository
        SignupViewModelFactory(userRepository)
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
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_authen)

        setupObservers()
        setupActions()
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.editSignupEmail.text?.clear()
        binding.editSignupFullname.text?.clear()
        binding.editSignupPassword.text?.clear()
        binding.editSignupConfirmPassword.text?.clear()
    }

    private fun setupObservers() {
        viewModel.signupFormState.observe(viewLifecycleOwner) {
            if (it.emailError != null) {
                isCorrectForm = false
                binding.editSignupEmail.error = getString(it.emailError)
            } else if (it.fullNameError != null) {
                isCorrectForm = false
                binding.editSignupFullname.error = getString(it.fullNameError)
            } else if (it.passwordError != null) {
                isCorrectForm = false
                binding.editSignupPassword.error = getString(it.passwordError)
            } else if (it.isCorrect) {
                isCorrectForm = true
            }
        }
    }

    private fun setupActions() {
        binding.btnSignupFragmentSignup.setOnClickListener {
            view?.let { view -> Utils.hideKeyBoard(view) }

            if (isCorrectForm) {
                val email = binding.editSignupEmail.text.toString()
                val fullName = binding.editSignupFullname.text.toString()
                val password = binding.editSignupPassword.text.toString()
                val confirmPassword = binding.editSignupConfirmPassword.text.toString()
                progressBar.visibility = View.VISIBLE

                signup(email, password, confirmPassword, fullName)
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.txt_require_enter_info),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.btnSignupFragmentLogin.setOnClickListener {
            view?.let { view -> Utils.hideKeyBoard(view) }
            navController.popBackStack()
        }

        binding.editSignupEmail.afterTextChanged {
            val fullName = binding.editSignupFullname.text.toString()
            val password = binding.editSignupPassword.text.toString()
            viewModel.signupFormChange(it, password, fullName)
        }

        binding.editSignupFullname.afterTextChanged {
            val email = binding.editSignupEmail.text.toString()
            val password = binding.editSignupPassword.text.toString()
            viewModel.signupFormChange(email, password, it)
        }

        binding.editSignupPassword.afterTextChanged {
            val email = binding.editSignupEmail.text.toString()
            val fullName = binding.editSignupFullname.text.toString()
            viewModel.signupFormChange(email, it, fullName)
        }

//        binding.editSignupEmail.setOnFocusChangeListener { _, hasFocus ->
////            if (!hasFocus) {
//                val email = binding.editSignupEmail.text.toString()
//                val fullName = binding.editSignupFullname.text.toString()
//                val password = binding.editSignupPassword.text.toString()
//                viewModel.signupFormChange(email, password, fullName)
////            }
//        }
//
//        binding.editSignupFullname.setOnFocusChangeListener { _, hasFocus ->
////            if (!hasFocus) {
//                val email = binding.editSignupEmail.text.toString()
//                val fullName = binding.editSignupFullname.text.toString()
//                val password = binding.editSignupPassword.text.toString()
//                viewModel.signupFormChange(email, password, fullName)
////            }
//        }
    }

    private fun signup(email: String, password: String, confirmPassword: String, fullName: String) {
        viewModel.signup(email, password, confirmPassword, fullName) {
            progressBar.visibility = View.GONE

            if (it != null) {
                Snackbar.make(
                    requireView(), getString(it), Snackbar.LENGTH_LONG
                ).show()
            }

            if (it == R.string.txt_check_your_email_signup) {
                navController.navigate(SignUpFragmentDirections.actionGlobalLoginFragment())
            }
        }
    }
}