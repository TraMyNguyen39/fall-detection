package com.example.falldetection.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.falldetection.databinding.ActivityDangerousAlertBinding

class DangerousAlertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangerousAlertBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangerousAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Tắt thông báo
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0) // Hủy thông báo với id là 0

        setUpViews()
    }

    private fun setUpViews() {
        val patientName = intent.getStringExtra(PATIENT_NAME) ?: "(Không rõ)"
        val patientEmail = intent.getStringExtra(PATIENT_EMAIL)
        val address = intent.getStringExtra(ADDRESS)
        val time = intent.getStringExtra(TIME)
        binding.textAlertName.text = patientName
//        binding.textAlertLocation.text = address
        binding.textAlertTime.text = time

        patientEmail?.let {
            binding.btnViewDetailAlert.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(PATIENT_EMAIL, patientEmail)
                intent.putExtra(PATIENT_NAME, patientName)
                startActivity(intent)
            }
        }

        binding.btnCancelNoti.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.progressBar.visibility = View.GONE
    }

    companion object {
        const val PATIENT_EMAIL = "PATIENT_EMAIL"
        const val PATIENT_NAME = "PATIENT_NAME"
        const val ADDRESS = "ADDRESS"
        const val TIME = "TIME"
    }
}