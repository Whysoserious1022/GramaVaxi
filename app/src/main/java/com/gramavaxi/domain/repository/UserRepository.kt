package com.gramavaxi.domain.repository

import com.gramavaxi.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /** Creates or updates the user document in Firestore users/{uid}. */
    suspend fun createOrUpdateUser(profile: UserProfile): Result<Unit>

    /** Returns a real-time Flow of the user profile for [uid]. */
    fun getUserProfile(uid: String): Flow<UserProfile?>
}
