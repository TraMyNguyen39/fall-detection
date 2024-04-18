package com.example.falldetection.ui.forget_password

import android.content.Context
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
import com.example.falldetection.databinding.FragmentForgetPasswordBinding
import com.example.falldetection.hideKeyBoard
import com.example.falldetection.ui.signup.SignUpFragmentDirections
import com.example.falldetection.viewmodel.UserViewModel
import com.example.falldetection.viewmodel.UserViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ForgetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgetPasswordBinding
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
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        progressBar = requireActivity().findViewById(R.id.progress_bar_authen)
        containerAuthentication = requireActivity().findViewById(R.id.container_authentication)

        setupObservers()
        setupActions()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.forgetPassMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                isCorrectForm = false
                binding.editForgetPassEmail.error = getString(it)
            } else {
                isCorrectForm = true
            }
        }
    }

    private fun setupActions() {
        binding.btnEnterNumberBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.btnConfirmPhoneNumber.setOnClickListener {
            view?.let { view -> hideKeyBoard(view) }

            if (isCorrectForm) {
                val email = binding.editForgetPassEmail.text.toString()
                progressBar.visibility = View.GONE
                forgetPassword(email)
            } else {
                Snackbar.make(
                    containerAuthentication,
                    getString(R.string.txt_require_enter_info),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.editForgetPassEmail.afterTextChanged {
            val email = binding.editForgetPassEmail.text.toString()
            viewModel.forgetPasswordFormChange(email)
        }
    }

    private fun forgetPassword(email: String) {
        view?.let { view -> hideKeyBoard(view) }

        viewModel.sendResetPassword(email) {
            Snackbar.make(
                containerAuthentication,
                getString(it),
                Snackbar.LENGTH_LONG
            ).show()

            if (it == R.string.txt_check_your_email) {
                navController.navigate(ForgetPasswordFragmentDirections.actionGlobalLoginFragment())
            }

            progressBar.visibility = View.GONE
        }
    }
}