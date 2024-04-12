package com.example.falldetection.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.falldetection.R
import com.example.falldetection.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView =binding.bottomNav
        bottomNavigationView.setupWithNavController(navController)

        // thiết lập nút Up
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.home_fragment, R.id.register_fragment, R.id.setting_fragment),
            )

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        onDestinationChanged()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_menu_item_profile) {
            logout()
        }
        return super.onOptionsItemSelected(item)
    }


    // cho phép bắt sự kiến nhấn Back
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    private fun logout() {
        auth.signOut()
        onBackPressedDispatcher.onBackPressed()
    }

    private fun onDestinationChanged() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home_fragment -> {
                    supportActionBar?.title = getString(R.string.title_home)
                    // do something if destination is home fragment
                }
                R.id.register_fragment -> {
                    // do something if destination is register fragment
                    supportActionBar?.title = getString(R.string.title_register)

                }
                R.id.setting_fragment -> {
                    // do something if destination is setting fragment
                    supportActionBar?.title = getString(R.string.title_setting)

                }
                else -> {
                    // do something
                }
            }
        }
    }
}