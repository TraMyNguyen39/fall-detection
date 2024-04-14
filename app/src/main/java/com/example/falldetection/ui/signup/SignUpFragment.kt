package com.example.falldetection.ui.signup

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.falldetection.MyApplication
import com.example.falldetection.R
import com.example.falldetection.afterTextChanged
import com.example.falldetection.databinding.FragmentSignupBinding
import com.example.falldetection.viewmodel.UserViewModel
import com.example.falldetection.viewmodel.UserViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding

    private lateinit var progressBar: ProgressBar
    private lateinit var containerAuthentication: View
    private lateinit var navController: NavController
    // Check if form is correct formatted
    private var isCorrectForm: Boolean = false
    private val TAG = "SIGNUP"

    private val viewModel: UserViewModel by activityViewModels {
        val userRepository = (requireActivity().application as MyApplication).userRepository
        UserViewModelFactory(userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_authen)
        containerAuthentication = requireActivity().findViewById(R.id.container_authentication)

        setupObservers()
        setupActions()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.signupFormState.observe(viewLifecycleOwner) {
            if (it.emailError != null) {
                isCorrectForm = false
                binding.editSignupEmail.error = getString(it.emailError)
            } else if (it.passwordError != null) {
                isCorrectForm = false
                binding.editSignupPassword.error = getString(it.passwordError)
            } else if (it.errorMessage != null) { // Khi dang ky loi
                Snackbar.make(
                    containerAuthentication,
                    getString(it.errorMessage),
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (it.isCorrect) {
                isCorrectForm = true
            }
        }
    }

    private fun setupActions() {
        binding.btnSignupFragmentSignup.setOnClickListener {
            hideKeyBoard()

            if (isCorrectForm) {
                val email = binding.editSignupEmail.text.toString()
                val password = binding.editSignupPassword.text.toString()
                val confirmPassword = binding.editSignupConfirmPassword.text.toString()
                progressBar.visibility = View.VISIBLE

                signup(email, password, confirmPassword)
            } else {
                Snackbar.make(
                    containerAuthentication,
                    getString(R.string.txt_require_enter_info),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.btnSignupFragmentLogin.setOnClickListener {
            hideKeyBoard()
            val action = SignUpFragmentDirections.actionGlobalLoginFragment()
            navController.navigate(action)
        }

        binding.editSignupEmail.afterTextChanged {
            val email = binding.editSignupEmail.text.toString()
            val password = binding.editSignupPassword.text.toString()
            viewModel.signupFormChange(email, password)
        }

        binding.editSignupPassword.afterTextChanged {
            val email = binding.editSignupEmail.text.toString()
            val password = binding.editSignupPassword.text.toString()
            viewModel.signupFormChange(email, password)
        }

//        binding.editSignupConfirmPassword.afterTextChanged {
//            val email = binding.editSignupEmail.text.toString()
//            val password = binding.editSignupPassword.text.toString()
//            val confirmPassword = binding.editSignupConfirmPassword.text.toString()
//            viewModel.signupFormChange(email, password)
//        }
    }
    private fun signup(email: String, password: String, confirmPassword: String) {
        val isEmailSentSuccessfully = viewModel.signup(email, password, confirmPassword) {
            if (it) {
                Snackbar.make(
                    containerAuthentication,
                    getString(R.string.txt_require_validate_account),
                    Snackbar.LENGTH_LONG
                ).show()
                navController.navigate(SignUpFragmentDirections.actionGlobalLoginFragment())
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun hideKeyBoard() {
        val inputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // on below line hiding our keyboard.
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}