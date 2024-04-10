package com.example.falldetection.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.falldetection.R
import com.example.falldetection.databinding.ActivityValidationBinding

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityValidationBinding
    private lateinit var navController: NavController
//    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_validation_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // thiết lập nút Up
//        appBarConfiguration = AppBarConfiguration(
//            setOf(R.id.home_fragment)
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
    }

//    // cho phép bắt sự kiến nhấn Back
//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }

//    private fun onDestinationChanged() {
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.home_fragment -> {
//                    supportActionBar?.title = getString(R.string.title_home)
//                    // do something if destination is home fragment
//                }
//                R.id.register_fragment -> {
//                    // do something if destination is register fragment
//                    supportActionBar?.title = getString(R.string.title_register)
//
//                }
//                R.id.setting_fragment -> {
//                    // do something if destination is setting fragment
//                    supportActionBar?.title = getString(R.string.title_setting)
//
//                }
//                else -> {
//                    // do something
//                }
//            }
//        }
//    }
}