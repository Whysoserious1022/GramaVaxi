package com.gramavaxi.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.gramavaxi.domain.model.UserProfile
import com.gramavaxi.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private fun userDoc(uid: String) = firestore.collection("users").document(uid)

    override suspend fun createOrUpdateUser(profile: UserProfile): Result<Unit> {
        return try {
            // Use merge so partial updates (e.g., language change) don't wipe other fields
            userDoc(profile.uid).set(
                profile.toMap().toMutableMap().also {
                    it["lastLoginAt"] = System.currentTimeMillis()
                },
                SetOptions.merge()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val listener = userDoc(uid).addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                trySend(null)
                return@addSnapshotListener
            }
            @Suppress("UNCHECKED_CAST")
            val data = snapshot.data as? Map<String, Any?>
            trySend(data?.let { UserProfile.fromMap(uid, it) })
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updatePhotoUrl(uid: String, photoUrl: String): Result<Unit> {
        return try {
            userDoc(uid).update("photoUrl", photoUrl).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
