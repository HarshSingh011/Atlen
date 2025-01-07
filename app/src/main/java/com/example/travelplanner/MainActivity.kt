package com.example.travelplanner

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.travelplanner.R
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var keepSplashScreenOn = true

        installSplashScreen().setKeepOnScreenCondition { keepSplashScreenOn }

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.authnavgraphControler) as NavHostFragment
        val navController = navHostFragment.navController

        lifecycleScope.launch {
            val token = DataStorageManager.getToken(this@MainActivity)
            val accountCreated = DataStorageManager.getAccountCreated(this@MainActivity)
            keepSplashScreenOn = false
            when {
                token != null -> navController.navigate(R.id.action_global_spalshScreen)
                accountCreated -> navController.navigate(R.id.action_global_loginWithPassword)
                else -> navController.navigate(R.id.action_global_onBoardingFragment)
            }
        }
    }

    private fun enableEdgeToEdge() {
    }
}

object DataStorageManager {
    fun getToken(activity: MainActivity): String? {
        //Implementation to get token
        return null
    }

    fun getAccountCreated(activity: MainActivity): Boolean {
        //Implementation to check account creation
        return false
    }
}
