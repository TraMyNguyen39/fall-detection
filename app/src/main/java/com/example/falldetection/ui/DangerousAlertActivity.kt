package com.example.falldetection.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.falldetection.databinding.ActivityDangerousAlertBinding


class DangerousAlertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangerousAlertBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangerousAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    companion object {
        const val PATIENT_EMAIL = "PATIENT_EMAIL"
        const val PATIENT_NAME = "PATIENT_NAME"
        const val ADDRESS = "ADDRESS"
        const val TIME = "TIME"
    }
}