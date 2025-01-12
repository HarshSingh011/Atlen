package com.example.travelplanner

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.example.travelplanner.DataClasses.LoginRequest
import com.example.travelplanner.DataStorage.DataStorageManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginWithPassword : Fragment() {
    private var email: String = ""

    private lateinit var dataStoreManager: DataStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStorageManager(requireContext()) // Initialize here
        arguments?.let {
            email = it.getString("email", "") ?: ""
            if (email.isEmpty()) {
                lifecycleScope.launch {
                    email = retrieveEmail()
                    Log.d("LoginWithPassword", "Email retrieved from DataStore: $email")
                }
            } else {
                Log.d("LoginWithPassword", "Email from arguments: $email")
            }
        }
    }

    private suspend fun retrieveEmail(): String {
        return dataStoreManager.getAccountEmail().first() ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycleScope.launch {
            if (email.isEmpty()) {
                email = retrieveEmail()
            }
            Log.d("LoginWithPasswordOnCreateView", "Email retrieved from DataStore: $email")
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
        )

        return ComposeView(requireContext()).apply {
            setContent {
                LoginScreen(
                    findNavController(),
                    email = email,
                    dataStoreManager = dataStoreManager
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    email: String,
    dataStoreManager: DataStorageManager
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showPasswordRequirements by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isPasswordFocused by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isLoading by remember { mutableStateOf(false) }

    val primaryColor = ContextCompat.getColor(context, R.color.primarycolor)
    val passwordBoxColor = Color(ContextCompat.getColor(context, R.color.passwordBox))
    val green = Color(ContextCompat.getColor(context, R.color.correctcolor))

    val passwordRequirements = listOf(
        "One uppercase letter" to { it: String -> it.any { char -> char.isUpperCase() } },
        "One lowercase letter" to { it: String -> it.any { char -> char.isLowerCase() } },
        "One number" to { it: String -> it.any { char -> char.isDigit() } },
        "One special character (&@$% etc.)" to { it: String -> it.any { char -> !char.isLetterOrDigit() } },
        "At least 8 characters" to { it: String -> it.length >= 8 }
    )

    fun isPasswordValid(pass: String): Boolean {
        return passwordRequirements.all { (_, check) -> check(pass) }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)

            ) {

                Spacer(modifier = Modifier.height(48.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Login with Password",
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .align(Alignment.CenterHorizontally),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    fontSize = 24.sp
                )

                Text(
                    text = "Password",
                    modifier = Modifier.padding(bottom = 18.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        showPasswordRequirements = true
                    },
                    placeholder = { Text("Password") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        androidx.compose.material.IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            androidx.compose.material.Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(remember { FocusRequester() })
                        .onFocusChanged { focusState ->
                            isPasswordFocused = focusState.isFocused
                            showPasswordRequirements = focusState.isFocused && password.isNotEmpty()
                        },
                    shape = RoundedCornerShape(15.dp)
                )

                TextButton(
                    onClick = {
                        navController.navigate(R.id.action_loginWithPassword_to_forgotPasswordEmailVerify, Bundle().apply {
                            putString("email", email)
                        })
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Forgot password?",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }

                AnimatedVisibility(
                    visible = showPasswordRequirements && isPasswordFocused,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    androidx.compose.material.Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = passwordBoxColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            androidx.compose.material.Text(
                                text = "Password must include:",
                                color = androidx.compose.material.MaterialTheme.colors.onSurface.copy(
                                    alpha = 0.7f
                                )
                            )
                            passwordRequirements.forEach { (requirement, check) ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    androidx.compose.material.Icon(
                                        painter = painterResource(
                                            id = if (check(password)) R.drawable.tick
                                            else R.drawable.ic_launcher_foreground
                                        ),
                                        contentDescription = null,
                                        tint = if (check(password)) green else Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    androidx.compose.material.Text(
                                        text = requirement,
                                        color = if (check(password)) green else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            when{
                                !isPasswordValid(password) -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Password does not meet requirements")
                                    }
                                }
                                else -> {
                                    try {
                                        isLoading = false
                                        val response = AuthRetrofitClient.instance.login(LoginRequest(email, password))
                                        if (response.data.access != null) {
                                            dataStoreManager.saveToken(response.data.access)
                                            Log.d("LoginWithPassword", "Token: ${dataStoreManager.getToken()}")
                                            navController.navigate(R.id.action_loginWithPassword_to_splashScreen,Bundle().apply {
                                                putString("email", email)
                                            })
                                        } else {
                                            snackbarHostState.showSnackbar(response.message)
                                        }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        snackbarHostState.showSnackbar("Login failed: ${e.message}")
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(48.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(primaryColor)
                    ),
                    shape = RoundedCornerShape(15.dp),
                    enabled = !isSuccess
                ) {
                    Text("Continue")
                }

                Text(
                    text = "By proceeding, you agree to our Terms of Use and Privacy Policy",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
        LoadingScreen(isLoading = isLoading)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        rememberNavController(),
        email = "",
        dataStoreManager = DataStorageManager(LocalContext.current)
    )
}