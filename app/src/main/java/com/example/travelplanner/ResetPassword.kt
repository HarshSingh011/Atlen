package com.example.travelplanner

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.travelplanner.DataClasses.Register
import com.example.travelplanner.DataClasses.ResetPasswordRequest
import com.example.travelplanner.R
import kotlinx.coroutines.launch


class ResetPassword : Fragment() {
    private var email: String = ""
    private var resetToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString("email") ?: ""
            resetToken = it.getString("reset_token") ?: ""
        }
        Log.d("ResetPassword", "Received email: $email, reset token: $resetToken")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        )

        return ComposeView(requireContext()).apply {
            setContent {
                resetpassword(findNavController(), email, resetToken)
            }
        }
    }
}

@Composable
fun resetpassword(
    navController: NavController,
    email: String,
    resetToken: String,
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showPasswordRequirements by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val passwordBoxColor = Color(ContextCompat.getColor(context, R.color.passwordBox))

    val snackbarHostState = remember { SnackbarHostState() }

    val passwordRequirements = listOf(
        "One uppercase letter" to password.any { it.isUpperCase() },
        "One lowercase letter" to password.any { it.isLowerCase() },
        "One number" to password.any { it.isDigit() },
        "One special character (&@$% etc.)" to password.any { !it.isLetterOrDigit() }
    )

    fun isPasswordValid(): Boolean {
        return password.length >= 6 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.White)
            ) {

                Spacer(modifier = Modifier.height(48.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Plan. Explore. Travel.",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "New Password",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        showPasswordRequirements = it.isNotEmpty()
                    },
                    placeholder = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            showPasswordRequirements = focusState.isFocused && password.isNotEmpty()
                        },
                    shape = RoundedCornerShape(15.dp)
                )

                AnimatedVisibility(visible = showPasswordRequirements) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = passwordBoxColor
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Password must be at least 6 characters long and include:",
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                            passwordRequirements.forEach { (requirement, isMet) ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (isMet) R.drawable.tick
                                            else R.drawable.ic_launcher_foreground
                                        ),
                                        contentDescription = null,
                                        tint = if (isMet) Color(0xFF6200EE) else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = requirement,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Confirm Password",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Confirm Password") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        when {
                            !isPasswordValid() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Password does not meet requirements")
                                }
                            }
                            password != confirmPassword -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Passwords do not match")
                                }
                            }
                            else -> {
                                scope.launch {
                                    try {
                                        val response = AuthRetrofitClient.instance.resetpassword(
                                            ResetPasswordRequest(
                                                email = email,
                                                confirm_password = confirmPassword,
                                                new_password = password,
                                                reset_token = resetToken
                                            )
                                        )

                                        if (response.success) {
                                            navController.navigate(R.id.action_resetPassword_to_loginWithPassword, Bundle().apply {
                                                putString("email", email)
                                            })
                                            snackbarHostState.showSnackbar("Password reset successfully")
                                        } else {
                                            snackbarHostState.showSnackbar(response.message)
                                        }
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Registration failed: ${e.message}")
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE))
                ) {
                    Text("Continue")
                }

                Text(
                    text = "By proceeding, you agree to our Terms of Use and Privacy Policy",
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter)
            ) { snackbarData ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color(0xFFFFEBEE),
                    contentColor = Color(0xFFB71C1C)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color(0xFFB71C1C)
                        )
                        Text(snackbarData.message)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun resetpasswordpreview() {
    resetpassword(rememberNavController(), email = "", resetToken = "")
}