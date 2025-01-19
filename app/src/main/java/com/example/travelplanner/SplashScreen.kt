package com.example.travelplanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.travelplanner.DataStorage.DataStorageManager
import kotlinx.coroutines.flow.first

class SplashScreen : ComponentActivity() {
    private lateinit var dataStoreManager: DataStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStorageManager(this)
        setContent {
            SplashScreenLayout(
                dataStorageManager = dataStoreManager
            )
        }
    }
}

@Composable
fun SplashScreenLayout(
    dataStorageManager: DataStorageManager
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedLogo()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Travel Planner",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        CheckAuthAndNavigate(
            dataStorageManager = dataStorageManager
        )
    }
}

@Composable
fun AnimatedLogo() {
    var animationState by remember { mutableStateOf(0) }
    val yOffset by animateFloatAsState(
        targetValue = when (animationState) {
            0 -> -200f
            1 -> 20f
            2 -> 0f
            else -> 0f
        },
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessLow
        )
    )

    val rotation by animateFloatAsState(
        targetValue = when (animationState) {
            3 -> 15f
            4 -> -15f
            5 -> 0f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 150, easing = LinearEasing)
    )

    LaunchedEffect(key1 = true) {
        delay(500)
        animationState = 1
        delay(300)
        animationState = 2
        delay(500)
        repeat(3) {
            animationState = 3
            delay(150)
            animationState = 4
            delay(150)
            animationState = 5
            delay(150)
        }
    }

    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "App Logo",
        modifier = Modifier
            .size(150.dp)
            .offset(y = yOffset.dp)
            .rotate(rotation),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun CheckAuthAndNavigate(
    dataStorageManager: DataStorageManager
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        delay(3000) // Increased delay to allow for animations

        val token = dataStorageManager.getToken().first()
        val accountCreated = dataStorageManager.getAccountToken().first()
        val accountEmail = dataStorageManager.getAccountEmail().first()

        Log.d("SplashScreen", "Token: $token, Account Created: $accountCreated, Account Gmail: $accountEmail")

        val intent = Intent(context, MainActivity::class.java)
        when {
            token != null -> {
                Log.d("SplashScreen", "Navigating to main content")
                intent.putExtra("destination", "main_content")
            }
            accountCreated != null -> {
                Log.d("SplashScreen", "Navigating to login with password")
                intent.putExtra("destination", "login_with_password")
            }
            else -> {
                Log.d("SplashScreen", "Navigating to onboarding")
                intent.putExtra("destination", "onboarding")
            }
        }
        context.startActivity(intent)
        (context as? ComponentActivity)?.finish()
    }
}

class DataStorageManager {
    fun getToken() = kotlinx.coroutines.flow.flow { emit("token") }
    fun getAccountToken() = kotlinx.coroutines.flow.flow { emit("accountCreated") }
    fun getAccountEmail() = kotlinx.coroutines.flow.flow { emit("email@example.com") }
}

