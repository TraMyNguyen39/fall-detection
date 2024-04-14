package com.example.falldetection.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.falldetection.data.model.User
import com.example.falldetection.databinding.FragmentLoginBinding
import com.example.falldetection.ui.MainActivity
import com.example.falldetection.viewmodel.UserViewModel
import com.example.falldetection.viewmodel.UserViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var containerAuthentication: View
    private lateinit var navController: NavController
    // Check if form is correct formatted
    private var isCorrectForm: Boolean = false

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_authen)
        containerAuthentication = requireActivity().findViewById(R.id.container_authentication)

        setupObservers()
        setupActions()
        return binding.root
    }

    private fun setupActions() {
        binding.btnForgetPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRequireValidationFragment()
            navController.navigate(action)
        }

        binding.btnSignUp.setOnClickListener {
            hideKeyBoard()

            val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            navController.navigate(action)
        }

        binding.btnSignIn.setOnClickListener {
            hideKeyBoard()

            if (isCorrectForm) {
                val email = binding.editEmailUsername.text.toString()
                val password = binding.editLoginPassword.text.toString()
                progressBar.visibility = View.VISIBLE

                login(email, password)
            } else {
                Snackbar.make(
                    containerAuthentication,
                    getString(R.string.txt_require_enter_info),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.editEmailUsername.afterTextChanged {
            val email = binding.editEmailUsername.text.toString()
            val password = binding.editLoginPassword.text.toString()
            viewModel.loginFormChange(email, password)
        }

        binding.editLoginPassword.afterTextChanged {
            val email = binding.editEmailUsername.text.toString()
            val password = binding.editLoginPassword.text.toString()
            viewModel.loginFormChange(email, password)
        }
    }

    private fun setupObservers() {
        viewModel.loginFormState.observe(viewLifecycleOwner) {
            if (it.emailError != null) {
                isCorrectForm = false
                binding.editEmailUsername.error = getString(it.emailError)
            } else if (it.passwordError != null) {
                isCorrectForm = false
                binding.editLoginPassword.error = getString(it.passwordError)
            } else if (it.errorMessage != null) { // Khi dang nhap loi
                Snackbar.make(
                    containerAuthentication,
                    getString(it.errorMessage),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                isCorrectForm = true
            }
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let { updateUI(it) }
        }
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password)
        progressBar.visibility = View.GONE
    }

    private fun updateUI(user: User) {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    private fun hideKeyBoard() {
        val inputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // on below line hiding our keyboard.
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}