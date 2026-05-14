package com.gramavaxi.presentation.screen.onboarding

import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.gramavaxi.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// SPLASH SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SplashScreen(
    onNavigateToLanguageSelect: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var logoVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(600), label = "logoAlpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        logoVisible = true
        delay(700)
        taglineVisible = true
        delay(1500)
        if (viewModel.isLoggedIn()) {
            onNavigateToDashboard()
        } else {
            onNavigateToLanguageSelect()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Primary, PrimaryContainer, Color(0xFF2E6B4F))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = 60.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
        )

        // Main logo block
        Column(
            modifier = Modifier
                .scale(logoScale)
                .alpha(logoAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🐄", fontSize = 52.sp)
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Grama-Vaxi",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "ಗ್ರಾಮ-ವ್ಯಾಕ್ಸಿ",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White.copy(0.85f),
                fontWeight = FontWeight.SemiBold
            )
        }

        // Bottom section
        AnimatedVisibility(
            visible = taglineVisible,
            enter = fadeIn() + slideInVertically { 40 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = Color.White.copy(0.75f),
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "AI-Powered · Offline-First · Rural-Ready",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(0.6f),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LANGUAGE SELECT SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LanguageSelectScreen(onLanguageSelected: () -> Unit) {
    val languages = listOf(
        Triple("English", "en", "🇬🇧"),
        Triple("ಕನ್ನಡ", "kn", "🌿")
    )
    var animIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); animIn = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Primary, PrimaryContainer))
            )
    ) {
        // Top decorative circle
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color.White.copy(0.05f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = animIn,
                enter = fadeIn() + slideInVertically { -60 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌍", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Select Language",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "ಭಾಷೆ ಆಯ್ಕೆ ಮಾಡಿ",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(0.8f)
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            languages.forEachIndexed { index, (label, _, flag) ->
                AnimatedVisibility(
                    visible = animIn,
                    enter = fadeIn(tween(400, delayMillis = 200 + index * 120))
                            + slideInVertically(tween(400, delayMillis = 200 + index * 120)) { 60 }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onLanguageSelected() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(flag, fontSize = 28.sp)
                            Spacer(Modifier.width(16.dp))
                            Text(
                                label,
                                style = MaterialTheme.typography.titleMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LOGIN SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current

    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // React to state changes
    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.Success           -> onLoginSuccess()
            is AuthUiState.PhoneOtpSent      -> verificationId = s.verificationId
            is AuthUiState.Error             -> { snackbarMessage = s.message; viewModel.clearError() }
            else                             -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {

        // ── Top green banner ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .background(
                    Brush.verticalGradient(listOf(Primary, PrimaryContainer, Color(0xFF2E6B4F)))
                )
        ) {
            // Decorative circle
            Box(
                Modifier
                    .size(180.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 50.dp, y = (-30).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.05f))
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(0.12f)),
                    contentAlignment = Alignment.Center
                ) { Text("🐄", fontSize = 38.sp) }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Grama-Vaxi",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "Smart Livestock Health",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.75f)
                )
            }
        }

        // ── Login Card ───────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 28.dp, vertical = 32.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    if (verificationId != null) "Enter OTP" else "Sign In to Continue",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (verificationId != null)
                        "OTP sent to +91 $phoneNumber"
                    else
                        "Access your livestock health records",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                if (verificationId == null) {
                    // ── Google Sign-In Button ──────────────────────────────
                    GoogleSignInButton(
                        isLoading = uiState is AuthUiState.Loading,
                        onClick = {
                            scope.launch {
                                launchGoogleSignIn(activity) { idToken ->
                                    viewModel.signInWithGoogle(idToken)
                                }
                            }
                        }
                    )

                    // ── Divider ─────────────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = OutlineVariant)
                        Text(
                            "  or continue with phone  ",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant
                        )
                        Divider(modifier = Modifier.weight(1f), color = OutlineVariant)
                    }

                    // ── Phone Field ─────────────────────────────────────────
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { if (it.length <= 10) phoneNumber = it },
                        label = { Text("Mobile Number") },
                        placeholder = { Text("10-digit number") },
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp)
                            ) {
                                Text("+91", fontWeight = FontWeight.Bold, color = Primary)
                                Spacer(Modifier.width(8.dp))
                                Divider(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(1.dp),
                                    color = OutlineVariant
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Secondary,
                            focusedLabelColor  = Secondary,
                            cursorColor        = Secondary
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = { viewModel.sendPhoneOtp(phoneNumber, activity!!) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        enabled = phoneNumber.length == 10 && uiState !is AuthUiState.Loading
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Send OTP",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                } else {
                    // ── OTP Input ───────────────────────────────────────────
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { if (it.length <= 6) otp = it },
                        label = { Text("6-digit OTP") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Secondary,
                            focusedLabelColor  = Secondary,
                            cursorColor        = Secondary
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = { viewModel.verifyOtp(verificationId!!, otp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        enabled = otp.length == 6 && uiState !is AuthUiState.Loading
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Verify OTP",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    TextButton(
                        onClick = { verificationId = null; otp = "" },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("← Change Number", color = Primary)
                    }
                }

                // Snackbar replacement — inline error
                snackbarMessage?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ErrorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            msg,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = OnErrorContainer
                        )
                    }
                    LaunchedEffect(msg) {
                        delay(4000)
                        snackbarMessage = null
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Google Sign-In Button Component
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun GoogleSignInButton(isLoading: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, OutlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceContainerLowest),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Primary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(
                "G",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4285F4)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "Sign in with Google",
                style = MaterialTheme.typography.labelLarge,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Google Credential Manager launcher helper
// ─────────────────────────────────────────────────────────────────────────────
private suspend fun launchGoogleSignIn(
    activity: Activity?,
    onToken: (String) -> Unit
) {
    if (activity == null) return
    try {
        val credentialManager = CredentialManager.create(activity)

        // Replace SERVER_CLIENT_ID with your actual Web Client ID from google-services.json
        // (found under oauth_client -> client_type 3 -> client_id)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(activity, request)
        val credential = result.credential

        if (credential is com.google.android.libraries.identity.googleid.GoogleIdTokenCredential) {
            onToken(credential.idToken)
        }
    } catch (e: GetCredentialException) {
        Log.e("GoogleSignIn", "Credential error: ${e.message}")
    } catch (e: Exception) {
        Log.e("GoogleSignIn", "Unexpected error: ${e.message}")
    }
}

/**
 * YOUR WEB CLIENT ID — paste the client_id from the oauth_client section
 * (client_type = 3) in your newly downloaded google-services.json here:
 */
private const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID_HERE"
