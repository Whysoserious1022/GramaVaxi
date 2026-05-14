package com.gramavaxi.domain.repository

import com.google.firebase.auth.FirebaseUser

/**
 * Defines all authentication operations available in Grama-Vaxi.
 * Implementations live in the data layer.
 */
interface AuthRepository {

    /** Returns the currently signed-in Firebase user, or null if not authenticated. */
    fun getCurrentUser(): FirebaseUser?

    /** Returns true if a user is currently signed in. */
    fun isLoggedIn(): Boolean

    /**
     * Signs in using a Google ID token obtained from the Credential Manager flow.
     * Returns [Result.success] with the FirebaseUser on success, or [Result.failure] on error.
     */
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>

    /**
     * Sends an OTP SMS to [phoneNumber] (E.164 format, e.g. "+919876543210").
     * The caller must provide [activity] for reCAPTCHA verification.
     * Invokes [onCodeSent] with the verificationId when the SMS is dispatched.
     * Invokes [onError] with an exception on failure.
     * Invokes [onAutoVerified] if instant-verification completes without OTP entry.
     */
    fun sendPhoneOtp(
        phoneNumber: String,
        activity: android.app.Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onAutoVerified: (FirebaseUser) -> Unit,
        onError: (Exception) -> Unit
    )

    /**
     * Verifies the OTP the user entered against the given [verificationId].
     * Returns [Result.success] with FirebaseUser on success, or [Result.failure] on error.
     */
    suspend fun verifyPhoneOtp(verificationId: String, otp: String): Result<FirebaseUser>

    /** Signs the current user out of Firebase. */
    fun signOut()
}
