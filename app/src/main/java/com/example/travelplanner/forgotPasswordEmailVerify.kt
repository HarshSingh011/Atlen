package com.example.travelplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import com.example.travelplanner.DataClasses.EmailVerify
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class forgotPasswordEmailVerify : Fragment() {
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString("email", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.loginWithPassword)
                }
            }
        )

        return ComposeView(requireContext()).apply {
            setContent {
                ForgotPasswordEmailVerify(findNavController())
            }
        }
    }
}

@Composable
fun ForgotPasswordEmailVerify(navController: androidx.navigation.NavController) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val primaryColor = ContextCompat.getColor(context, R.color.primarycolor)
    val passwordBoxColor = Color(ContextCompat.getColor(context, R.color.passwordBox))
    val green = Color(ContextCompat.getColor(context, R.color.correctcolor))

    var isSnackbarActive by remember { mutableStateOf(false) }

    LaunchedEffect(isSnackbarActive) {
        if (isSnackbarActive) {
            delay(3000)
            snackbarHostState.currentSnackbarData?.dismiss()
            isSnackbarActive = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 24.dp)
                    .size(80.dp)
            )

            Text(
                text = "Forgot your password?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "No problem, traveller! Provide your email, and we'll send an OTP to reset your password instantly.",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Email address",
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                placeholder = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true,
            )

            Button(
                onClick = {
                    if (!isSnackbarActive) {
                        isLoading = true
                        if (email.isNotEmpty()) {
                            if (email.matches(emailRegex)) {
                                coroutineScope.launch {
                                    try {
                                        val response = AuthRetrofitClient.instance.verifyEmailForgot(EmailVerify(email))
                                        isLoading = false
                                        if (response.success) {
                                            navController.navigate(R.id.action_forgotPasswordEmailVerify_to_forgotPasswordOtpValidation, Bundle().apply {
                                                putString("email", email)
                                            })
                                        } else {
                                            val message = JSONObject(response.message).getString("message")
                                            isSnackbarActive = true
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    } catch (e: HttpException) {
                                        isLoading = false
                                        val errorBody = e.response()?.errorBody()?.string()
                                        val error = JSONObject(errorBody).getString("message")
                                        isSnackbarActive = true
                                        snackbarHostState.showSnackbar("Email Verify: $error")
                                    } catch (e: Exception) {
                                        isLoading = false
                                        errorMessage = "Failed to send OTP: ${e.message}"
                                        isSnackbarActive = true
                                        snackbarHostState.showSnackbar(errorMessage)
                                    }
                                }
                            } else {
                                isLoading = false
                                isSnackbarActive = true
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please enter a valid email")
                                }
                            }
                        } else {
                            isLoading = false
                            isSnackbarActive = true
                            scope.launch {
                                snackbarHostState.showSnackbar("Please enter your email")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isSnackbarActive) Color.Gray else Color(primaryColor)
                ),
                enabled = !isLoading && !isSnackbarActive
            ) {
                Text(
                    text = "Send OTP",
                    color = if (isSnackbarActive) Color.Black else Color.White,
                    fontSize = 16.sp
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp)
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
                        tint = Color(0xFFB71C1C),
                        modifier = Modifier.clickable {
                            snackbarData.dismiss()
                            isSnackbarActive = false
                        }
                    )
                    Text(snackbarData.message)
                }
            }
        }

        LoadingScreen(isLoading = isLoading)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForgotPasswordEmailVerify() {
    ForgotPasswordEmailVerify(rememberNavController())
}