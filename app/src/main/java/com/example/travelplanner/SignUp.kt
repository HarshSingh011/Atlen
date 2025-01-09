package com.example.travelplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
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
                SignUpScreen(findNavController(), email, onNavigateBack = {})
            }
        }
    }
}

@Composable
fun SignUpScreen(
    navController: NavController,
    email: String,
    onNavigateBack: () -> Unit
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
    val primaryColor = ContextCompat.getColor(context, R.color.primarycolor)
    val passwordBoxColor = Color(ContextCompat.getColor(context, R.color.passwordBox))
    val green = Color(ContextCompat.getColor(context, R.color.correctcolor))

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
            ) {
                IconButton(onClick = { navController.navigate(R.id.frontPage) }) {
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
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .wrapContentHeight()
                    ){
                        Text(
                            text = "First Name",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(bottom = 24.dp),
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .wrapContentHeight()
                    ){
                        Text(
                            text = "Last Name",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(bottom = 24.dp),
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = { Text("First Name") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(15.dp)
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = { Text("Last Name") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(15.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Password",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 24.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = passwordBoxColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
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
//                                        tint = if (isMet) Color(0xFF6200EE) else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = requirement,
                                        color = if (isMet) green else Color.Black
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
                        .padding(bottom = 24.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                                            navController.navigate(R.id.action_signUp_to_otpValidation, bundle)
                                        } else {
                                            val message = JSONObject(response.message).getString("message")
                                            println(message)
                                            snackbarHostState.showSnackbar(message)
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
                                snackbarHostState.currentSnackbarData?.dismiss() // Dismiss snackbar on icon click
                            }
                        )
                        Text(snackbarData.message)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(rememberNavController(), email = "", onNavigateBack = {})
}

