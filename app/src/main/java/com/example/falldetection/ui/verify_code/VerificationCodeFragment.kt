package com.example.falldetection.ui.verify_code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.falldetection.databinding.FragmentVerificationCodeBinding

class VerificationCodeFragment : Fragment() {
    private lateinit var binding : FragmentVerificationCodeBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerificationCodeBinding.inflate(inflater, container, false)

        binding.btnVerificationBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.btnConfirmVerificationCode.setOnClickListener {
            // TODO
        }

        return binding.root
    }
}