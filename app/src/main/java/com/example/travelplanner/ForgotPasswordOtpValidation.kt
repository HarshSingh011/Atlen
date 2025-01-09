package com.example.travelplanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import com.example.travelplanner.DataClasses.EmailVerify
import com.example.travelplanner.DataClasses.ForgotPasswordOtpVerify
import com.example.travelplanner.DataClasses.Register
import com.example.travelplanner.DataClasses.Registrationotp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForgotPasswordOtpValidation : Fragment() {
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString("email") ?: ""
        }
        Log.d("ForgotPasswordOtpValidation", "Received email: $email")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
                OtpValidationForgotScreen(
                    email = email,
                    scope = lifecycleScope,
                    navController = findNavController()
                )
            }
        }
    }
}

@Composable
fun OtpValidationForgotScreen(
    email: String,
    scope: kotlinx.coroutines.CoroutineScope,
    navController: NavController
) {
    var otpValue by remember { mutableStateOf(List(6) { "" }) }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var canResend by remember { mutableStateOf(true) }
    var remainingTime by remember { mutableStateOf(0) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarType by remember { mutableStateOf(SnackbarType.SUCCESS) }

    val focusRequesters = remember { List(6) { FocusRequester() } }
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
            delay(1000)
            remainingTime--
            if (remainingTime == 0) {
                canResend = true
            }
        }
    }

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage.isNotEmpty()) {
            scaffoldState.snackbarHostState.showSnackbar(snackbarMessage)
            snackbarMessage = ""
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                SnackbarHost(it) { data ->
                    CustomSnackbar(
                        message = data.message,
                        type = snackbarType,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = "Enter the code",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Enter the OTP code we have sent to your email",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                otpValue.forEachIndexed { index, digit ->
                    OtpTextField(
                        value = digit,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1) {
                                val newOtpValue = otpValue.toMutableList()
                                newOtpValue[index] = newValue
                                otpValue = newOtpValue

                                if (newValue.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        onBackspace = {
                            val newOtpValue = otpValue.toMutableList()
                            when {
                                index > 0 && newOtpValue[index].isEmpty() -> {
                                    newOtpValue[index - 1] = ""
                                    otpValue = newOtpValue
                                    focusRequesters[index - 1].requestFocus()
                                }
                                else -> {
                                    newOtpValue[index] = ""
                                    otpValue = newOtpValue
                                }
                            }
                        },
                        modifier = Modifier.focusRequester(focusRequesters[index]),
                        isError = isError,
                        focusRequester = focusRequesters[index]
                    )
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        val enteredOtp = otpValue.joinToString("")
                        if (enteredOtp.length != 6) {
                            snackbarType = SnackbarType.ERROR
                            snackbarMessage = "Please enter complete OTP"
                            isError = true
                            return@launch
                        }

                        try {
                            isLoading = true
                            isError = false
                            val response = AuthRetrofitClient.instance.resetpasswordOtp(
                                ForgotPasswordOtpVerify(
                                    email = email,
                                    otp = enteredOtp,
                                    verification_type = "password_reset"
                                )
                            )

                            if (response.success) {
                                snackbarType = SnackbarType.SUCCESS
                                snackbarMessage = "OTP verified successfully"
                                val resetToken = response.data.reset_token
                                navController.navigate(R.id.action_forgotPasswordOtpValidation_to_resetPassword, Bundle().apply {
                                    putString("email", email)
                                    putString("reset_token", resetToken)
                                })
                            } else {
                                isError = true
                                snackbarType = SnackbarType.ERROR
                                snackbarMessage = response.message ?: "Invalid OTP"
                            }
                        } catch (e: Exception) {
                            isError = true
                            snackbarType = SnackbarType.ERROR
                            snackbarMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4E4EFF),
                    disabledBackgroundColor = Color(0xFF4E4EFF).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Verify and Continue",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            TextButton(
                onClick = {
                    if (canResend) {
                        scope.launch {
                            try {
                                isLoading = true
                                val response = AuthRetrofitClient.instance.verifyEmailForgot(
                                    EmailVerify(email)
                                )

                                if (response.success) {
                                    snackbarType = SnackbarType.WARNING
                                    snackbarMessage = "OTP resent successfully"
                                    remainingTime = 60
                                    canResend = false
                                    otpValue = List(6) { "" }
                                    isError = false
                                    focusRequesters[0].requestFocus()
                                } else {
                                    snackbarType = SnackbarType.ERROR
                                    snackbarMessage = "Failed to resend OTP"
                                }
                            } catch (e: Exception) {
                                snackbarType = SnackbarType.ERROR
                                snackbarMessage = "Failed to resend OTP"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp),
                enabled = canResend && !isLoading
            ) {
                Text(
                    text = "Didn't receive the code?",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = if (remainingTime > 0)
                        " ${String.format("%02d:%02d", remainingTime / 60, remainingTime % 60)}"
                    else
                        " Resend OTP",
                    color = if (remainingTime > 0) Color.Gray else Color(0xFF4E4EFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreviewForgot() {
    OtpValidationForgotScreen(
        email = "",
        scope = rememberCoroutineScope(),
        navController = rememberNavController()
    )
}


