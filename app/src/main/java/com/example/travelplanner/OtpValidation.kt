package com.example.travelplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import com.example.travelplanner.DataClasses.Register
import com.example.travelplanner.DataClasses.Registrationotp
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.example.travelplanner.DataStorage.DataStorageManager

class OtpValidation : Fragment() {
    private lateinit var email: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var password: String
    private lateinit var confirmPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString("email") ?: ""
            firstName = it.getString("first_name") ?: ""
            lastName = it.getString("last_name") ?: ""
            password = it.getString("password") ?: ""
            confirmPassword = it.getString("confirm_password") ?: ""
        }
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
                    findNavController().navigate(R.id.otpValidation)
                }
            }
        )

        return ComposeView(requireContext()).apply {
            setContent {
                OtpValidationScreen(
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    password = password,
                    confirmPassword = confirmPassword,
                    scope = lifecycleScope,
                    navController = findNavController(),
                    context = requireContext()
                )
            }
        }
    }
}

@Composable
fun OtpValidationScreen(
    email: String,
    firstName: String,
    lastName: String,
    password: String,
    confirmPassword: String,
    scope: kotlinx.coroutines.CoroutineScope,
    navController: NavController,
    context : android.content.Context
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
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
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
            Spacer(modifier = Modifier.height(48.dp))

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
                            val response = AuthRetrofitClient.instance.restrationOtp(
                                Registrationotp(
                                    email = email,
                                    otp = enteredOtp,
                                    verification_type = "registration"
                                )
                            )

                            if (response.success) {
                                snackbarType = SnackbarType.SUCCESS
                                snackbarMessage = "OTP verified successfully"
                                DataStorageManager.saveAccountCreated(context, true)
                                navController.navigate(R.id.action_otpValidation_to_loginWithPassword, Bundle().apply {
                                    putString("email", email)
                                })
                            } else {
                                isError = true
                                val message = JSONObject(response.message).getString("message")
                                snackbarType = SnackbarType.ERROR
                                snackbarMessage = message
                            }
                        } catch (e: Exception) {
                            isError = true
                            snackbarType = SnackbarType.ERROR
                            snackbarMessage = "Invalid OTP"
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
                                val response = AuthRetrofitClient.instance.register(
                                    Register(
                                        email = email,
                                        first_name = firstName,
                                        last_name = lastName,
                                        password = password,
                                        confirm_password = confirmPassword
                                    )
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

@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    focusRequester: FocusRequester
) {
    val borderColor = when {
        isError -> Color(0xFFFF3B30)
        value.isNotEmpty() -> Color(0xFF4E4EFF)
        else -> Color(0xFFE5E5E5)
    }

    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.isEmpty()) {
                onBackspace()
            } else {
                onValueChange(newValue.take(1))
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (isError) Color(0xFFFF3B30) else Color.Black
        ),
        modifier = modifier
            .size(48.dp)
            .background(
                color = if (isError) Color(0xFFFFEBEA) else Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp && event.key == Key.Backspace) {
                    if (value.isEmpty()) {
                        onBackspace()
                    } else {
                        onValueChange("")
                    }
                    true
                } else {
                    false
                }
            },
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                innerTextField()
            }
        },
        singleLine = true
    )
}

@Composable
fun CustomSnackbar(
    message: String,
    type: SnackbarType,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        color = when (type) {
            SnackbarType.SUCCESS -> Color(0xFF4CAF50)
            SnackbarType.ERROR -> Color(0xFFFF3B30)
            SnackbarType.WARNING -> Color(0xFFFFB800)
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = when (type) {
                    SnackbarType.ERROR -> painterResource(id = R.drawable.ic_launcher_foreground)
                    SnackbarType.WARNING -> painterResource(id = R.drawable.ic_launcher_foreground)
                    SnackbarType.SUCCESS -> painterResource(id = R.drawable.ic_launcher_foreground)
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        snackbarHostState.currentSnackbarData?.dismiss() // Dismiss snackbar on icon click
                    }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

enum class SnackbarType {
    SUCCESS,
    ERROR,
    WARNING
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OtpValidationScreen(
        email = "",
        firstName = "",
        lastName = "",
        password = "",
        confirmPassword = "",
        scope = rememberCoroutineScope(),
        navController = rememberNavController(),
        context = LocalContext.current
    )
}


