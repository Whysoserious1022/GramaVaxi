package com.gramavaxi.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.gramavaxi.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user?.let { Result.success(it) }
                ?: Result.failure(Exception("Google sign-in returned no user"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun sendPhoneOtp(
        phoneNumber: String,
        activity: android.app.Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onAutoVerified: (FirebaseUser) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-verified (e.g., same device SIM)
                firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener { result ->
                        result.user?.let { onAutoVerified(it) }
                    }
                    .addOnFailureListener { onError(it) }
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                onError(e)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                onCodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun verifyPhoneOtp(verificationId: String, otp: String): Result<FirebaseUser> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user?.let { Result.success(it) }
                ?: Result.failure(Exception("OTP verification returned no user"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
