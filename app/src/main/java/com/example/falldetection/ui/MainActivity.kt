package com.example.falldetection.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.falldetection.R
import com.example.falldetection.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            postNotification()
        } else {
            showMessage(R.string.msg_permission_denied, Snackbar.LENGTH_LONG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNav
        bottomNavigationView.setupWithNavController(navController)

        // thiết lập nút Up
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.home_fragment, R.id.register_fragment, R.id.profile_fragment),
        )

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        onDestinationChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.main_menu_item_profile) {
//            logout()
//        }
        return super.onOptionsItemSelected(item)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val selfPms = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            val shouldShowPrompt =
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            if (selfPms == PackageManager.PERMISSION_GRANTED) {
                postNotification()
            } else if (shouldShowPrompt) {
                showMessage(R.string.msg_permission_prompt, Snackbar.LENGTH_LONG, true)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            Log.e("Notification", Build.VERSION.SDK_INT.toString())
        }
    }

    private fun postNotification() {
        // do something
    }

    private fun showMessage(messageId: Int, duration: Int, showAction: Boolean = false) {
        val snackBar = Snackbar.make(binding.root, messageId, duration)
        if (showAction && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            snackBar.setAction("OK") {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            snackBar.setAction("No thank") {
                showMessage(R.string.msg_permission_denied, Snackbar.LENGTH_LONG)
            }
        }
        snackBar.show()
    }


    // cho phép bắt sự kiến nhấn Back
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
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

                R.id.profile_fragment -> {
                    // do something if destination is setting fragment
                    supportActionBar?.title = getString(R.string.title_profile)
                }

                else -> {
                    // do something
                }
            }
        }
    }
}