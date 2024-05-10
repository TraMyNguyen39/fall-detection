package com.example.falldetection.ui.login

import android.content.Context
import android.content.Intent
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
import com.example.falldetection.databinding.FragmentLoginBinding
import com.example.falldetection.ui.MainActivity
import com.example.falldetection.utils.Utils
import com.example.falldetection.utils.Utils.afterTextChanged
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var navController: NavController

    // Check if form is correct formatted
    private var isCorrectForm: Boolean = false

    private val viewModel: LoginViewModel by activityViewModels {
        val userRepository = (requireActivity().application as MyApplication).userRepository
        LoginViewModelFactory(userRepository)
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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_authen)

        setupObservers()
        setupActions()
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.editEmailUsername.text?.clear()
        binding.editLoginPassword.text?.clear()

    }
    private fun setupActions() {
        binding.btnForgetPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRequireValidationFragment()
            navController.navigate(action)
        }

        binding.btnSignUp.setOnClickListener {
            view?.let { view -> Utils.hideKeyBoard(view) }

            val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            navController.navigate(action)
        }

        binding.btnSignIn.setOnClickListener {
            view?.let { view -> Utils.hideKeyBoard(view) }

            if (isCorrectForm) {
                val email = binding.editEmailUsername.text.toString()
                val password = binding.editLoginPassword.text.toString()
                progressBar.visibility = View.VISIBLE

                login(email, password)
            } else {
                Snackbar.make(
                    requireView(),
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
            } else {
                isCorrectForm = true
            }
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                progressBar.visibility = View.GONE
                Snackbar.make(
                    requireView(),
                    "Bạn ${user.fullName} đã đăng nhập thành công.", Snackbar.LENGTH_LONG
                ).show()

                // Lưu account vào preference
                saveToCurrentAccountPreference(user.email, user.fullName)
                // Sau đó, reset lại user để khi logout từ Activity khác không vào t/h user != null
                viewModel.resetAccount()

                directToHome()
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password) {
            if (it != null) {
                Snackbar.make(
                    requireView(), getString(it), Snackbar.LENGTH_LONG
                ).show()
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun directToHome() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    private fun saveToCurrentAccountPreference(
        email: String, displayName: String, imageUrl: String? = null
    ) {
        val sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_account_key), Context.MODE_PRIVATE
        )

        with(sharedPref.edit()) {
            putString(getString(R.string.preference_email_key), email)
            putString(getString(R.string.preference_display_name_key), displayName)
//            putString(getString(R.string.preference_dislay_name_key), imageUrl)
            apply()
        }
    }
}