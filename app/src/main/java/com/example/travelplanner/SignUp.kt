package com.example.travelplanner

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.example.travelplanner.DataClasses.Register
import kotlinx.coroutines.launch
import org.json.JSONObject

class SignUp : Fragment() {
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString("email") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SignUpScreen(findNavController(), email)
            }
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    email: String
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showPasswordRequirements by remember { mutableStateOf(false) }
    var showConfirmPasswordRequirements by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmPasswordFocused by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isLoading by remember { mutableStateOf(false) }

    val primaryColor = ContextCompat.getColor(context, R.color.primarycolor)
    val passwordBoxColor = Color(ContextCompat.getColor(context, R.color.passwordBox))
    val green = Color(ContextCompat.getColor(context, R.color.correctcolor))

    val snackbarHostState = remember { SnackbarHostState() }

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
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                IconButton(onClick = { navController.navigate(R.id.emailVerify) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Plan. Explore. Travel.",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 24.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it.trim() },
                        placeholder = { Text("First Name") },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(remember { FocusRequester() }),
                        shape = RoundedCornerShape(15.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Next) }
                        )
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it.trim() },
                        placeholder = { Text("Last Name") },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(remember { FocusRequester() }),
                        shape = RoundedCornerShape(15.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Next) }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Password",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
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
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
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

                AnimatedVisibility(
                    visible = showPasswordRequirements && isPasswordFocused,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = passwordBoxColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Password must include:",
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                            passwordRequirements.forEach { (requirement, check) ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (check(password)) R.drawable.tick
                                            else R.drawable.ic_launcher_foreground
                                        ),
                                        contentDescription = null,
                                        tint = if (check(password)) green else Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = requirement,
                                        color = if (check(password)) green else Color.Black
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
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        showConfirmPasswordRequirements = true
                    },
                    placeholder = { Text("Confirm Password") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(remember { FocusRequester() })
                        .onFocusChanged { focusState ->
                            isConfirmPasswordFocused = focusState.isFocused
                            showConfirmPasswordRequirements = focusState.isFocused && password.isNotEmpty()
                        },
                    shape = RoundedCornerShape(15.dp)
                )

                AnimatedVisibility(
                    visible = showConfirmPasswordRequirements && isConfirmPasswordFocused,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = passwordBoxColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Confirm password must:",
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                            passwordRequirements.forEach { (requirement, check) ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (check(confirmPassword)) R.drawable.tick
                                            else R.drawable.ic_launcher_foreground
                                        ),
                                        contentDescription = null,
                                        tint = if (check(confirmPassword)) green else Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = requirement,
                                        color = if (check(confirmPassword)) green else Color.Black
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (password == confirmPassword) R.drawable.tick
                                        else R.drawable.ic_launcher_foreground
                                    ),
                                    contentDescription = null,
                                    tint = if (password == confirmPassword) green else Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Match the password",
                                    color = if (password == confirmPassword) green else Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val bundle = Bundle()
                        bundle.putString("email", email)
                        bundle.putString("password", password)
                        bundle.putString("first_name", firstName)
                        bundle.putString("last_name", lastName)
                        bundle.putString("confirm_password", confirmPassword)
                        when {
                            firstName.isEmpty() || lastName.isEmpty() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please fill in all fields")
                                }
                            }
                            !isPasswordValid(password) -> {
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
                                isLoading = true
                                scope.launch {
                                    try {
                                        val response = AuthRetrofitClient.instance.register(
                                            Register(
                                                email = email,
                                                first_name = firstName,
                                                last_name = lastName,
                                                password = password,
                                                confirm_password = confirmPassword
                                            )
                                        )
                                        isLoading = false
                                        if (response.success) {
                                            navController.navigate(R.id.action_signUp_to_otpValidation, bundle)
                                        } else {
                                            val message = JSONObject(response.message).getString("message")
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        snackbarHostState.showSnackbar("Registration failed: ${e.message}")
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(primaryColor))
                ) {
                    Text(
                        "Continue",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                                snackbarHostState.currentSnackbarData?.dismiss()
                            }
                        )
                        Text(snackbarData.message)
                    }
                }
            }
            LoadingScreen(isLoading = isLoading)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(rememberNavController(), email = "")
}
