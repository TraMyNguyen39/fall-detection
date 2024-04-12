package com.example.falldetection.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.falldetection.databinding.FragmentSignupBinding
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth
    private val TAG = "SIGNUP"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        binding.btnSignUp.setOnClickListener {
            val action = SignUpFragmentDirections.actionGlobalLoginFragment()
            navController.navigate(action)
        }

        binding.btnSignIn.setOnClickListener {
            // TODO
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
//        login("nguyentramy19112003@gmail.com")

        if (auth.currentUser != null) {
            Log.d(TAG, auth.currentUser!!.email.toString())
        } else {
            Log.d(TAG, "Khong co duoc")
        }
    }
    private fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "create successfully. Please verify your email")
                            } else {
                                Log.w(TAG, "can not send email to you", task.exception)
                            }
                        }
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "create :failure", task.exception)
//                    updateUI(null)
                }
            }
    }

}