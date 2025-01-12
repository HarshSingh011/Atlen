package com.example.travelplanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.travelplanner.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.authnavgraphControler) as NavHostFragment
        val navController = navHostFragment.navController

        val destination = intent.getStringExtra("destination")
        when (destination) {
            "main_content" -> navController.navigate(R.id.action_global_loginsuccessful)
            "login_with_password" -> navController.navigate(R.id.action_global_loginWithPassword)
            "onboarding" -> navController.navigate(R.id.action_global_onBoardingFragment)
        }
    }
}

