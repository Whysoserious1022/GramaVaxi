package com.gramavaxi.presentation.screen.onboarding

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.gramavaxi.domain.model.UserProfile
import com.gramavaxi.domain.repository.AuthRepository
import com.gramavaxi.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    data class PhoneOtpSent(val verificationId: String) : AuthUiState()
    data class Success(val user: FirebaseUser)           : AuthUiState()
    data class Error(val message: String)               : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** Returns true if a user is already signed in (used by SplashScreen). */
    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    /** Called from Google Credential Manager result — passes the ID token to Firebase. */
    fun signInWithGoogle(idToken: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    persistUserToFirestore(user)
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Google sign-in failed")
                }
        }
    }

    /** Sends an OTP to the given phone number. */
    fun sendPhoneOtp(phone: String, activity: Activity) {
        if (phone.length < 10) {
            _uiState.value = AuthUiState.Error("Please enter a valid 10-digit phone number")
            return
        }
        _uiState.value = AuthUiState.Loading
        val fullPhone = if (phone.startsWith("+")) phone else "+91$phone"
        authRepository.sendPhoneOtp(
            phoneNumber  = fullPhone,
            activity     = activity,
            onCodeSent   = { verificationId ->
                _uiState.value = AuthUiState.PhoneOtpSent(verificationId)
            },
            onAutoVerified = { user ->
                viewModelScope.launch {
                    persistUserToFirestore(user)
                    _uiState.value = AuthUiState.Success(user)
                }
            },
            onError = { e ->
                _uiState.value = AuthUiState.Error(e.message ?: "Failed to send OTP")
            }
        )
    }

    /** Verifies the OTP the user typed in. */
    fun verifyOtp(verificationId: String, otp: String) {
        if (otp.length != 6) {
            _uiState.value = AuthUiState.Error("Enter the 6-digit OTP")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.verifyPhoneOtp(verificationId, otp)
                .onSuccess { user ->
                    persistUserToFirestore(user)
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "OTP verification failed")
                }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState.Idle
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }

    private suspend fun persistUserToFirestore(user: FirebaseUser) {
        val profile = UserProfile(
            uid      = user.uid,
            name     = user.displayName ?: "",
            phone    = user.phoneNumber ?: "",
            photoUrl = user.photoUrl?.toString() ?: ""
        )
        userRepository.createOrUpdateUser(profile)
    }
}
