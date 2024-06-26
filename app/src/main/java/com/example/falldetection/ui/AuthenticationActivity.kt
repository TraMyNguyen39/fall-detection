package com.example.falldetection.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.falldetection.R
import com.example.falldetection.databinding.ActivityAuthenticationBinding
import com.example.falldetection.utils.Utils
import com.google.firebase.messaging.FirebaseMessaging

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Save token
        retrieveToken()

        // Check if logined
        if (Utils.getCurrentEmail(this) != null) {
            directToHome()
        }

        // If not logined, load nav host
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_validation_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun directToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun retrieveToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Utils.token = task.result
                Log.e("Token", task.result)
            }
        }
    }
}