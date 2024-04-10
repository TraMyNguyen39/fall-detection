package com.example.falldetection.ui.forget_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.falldetection.databinding.FragmentForgetPasswordBinding

class ForgetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgetPasswordBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        binding.btnEnterNumberBack.setOnClickListener {
//            val action = ForgetPasswordFragmentDirections.actionGlobalLoginFragment()
//            findNavController().navigate(action)
            navController.popBackStack()
        }

        binding.btnConfirmPhoneNumber.setOnClickListener {
            val action =
                ForgetPasswordFragmentDirections.actionForgetPasswordToVerificationCodeFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }
}