package com.example.travelplanner

import android.content.Context
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.travelplanner.DataClasses.googleOauth
import com.example.travelplanner.DataStorage.DataStorageManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import retrofit2.HttpException

class FrontPage : Fragment() {

    private val TAG = "FrontPage"
    private lateinit var dataStoreManager: DataStorageManager
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signInLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                when {
                    idToken != null -> {
                        Log.d(TAG, "Got ID token.")
                        convertToken(idToken)
                    }
                    else -> {
                        Log.d(TAG, "No ID token!")
                    }
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed", e)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataStoreManager = DataStorageManager(requireContext())

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
                AccountOption(findNavController())
            }
        }
    }

    private fun signInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    signInLauncher.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                Log.d(TAG, e.localizedMessage)
            }
    }

    private fun convertToken(idToken: String) {
        Log.d(TAG, "Client ID: ${getString(R.string.default_web_client_id)}, ID Token: $idToken")
        lifecycleScope.launch {
            try {
                val response = AuthRetrofitClient.instance.googleOauth(
                    googleOauth(
                        access_token = idToken,
                        is_test = true
                    )
                )

                if (response.success == true) {
                    Log.d(TAG, "Token conversion successful")
                    dataStoreManager.saveToken(response.data.tokens.access)
                    findNavController().navigate(R.id.action_frontPage_to_loginSuccessful)
                } else {
                    Log.e(TAG, "Token conversion failed: Access token is null")
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Toast.makeText(requireContext(), "Failed to authenticate: $errorBody", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to convert token", e)
                Toast.makeText(requireContext(), "Failed to authenticate: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    @Composable
    fun AccountOption(navController: NavController) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Trippin Logo",
                    modifier = Modifier
                        .size(64.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Sign in to unlock the best of Trippin",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        Log.d(TAG, "Google Sign In button clicked")
                        if (isNetworkAvailable()) {
                            signInWithGoogle()
                        } else {
                            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.devicon_google),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            "Continue with Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    }
                }

                OutlinedButton(
                    onClick = { navController.navigate(R.id.action_frontPage_to_emailVerify) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.envelopesimple),
                            contentDescription = "Email Icon",
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            "Continue with mail",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "By proceeding, you agree to our Terms of Use and Privacy Policy.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

