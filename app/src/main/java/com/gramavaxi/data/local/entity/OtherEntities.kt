package com.gramavaxi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey val recordId: String,
    val animalId: String,
    val recordDate: Long,
    val symptoms: String?,
    val diagnosis: String?,
    val treatment: String?,
    val medications: String?,        // JSON list of medications
    val vetId: String?,
    val vetName: String?,
    val photoUri: String?,
    val aiSuggestion: String?,
    val urgencyLevel: String?,       // LOW / MEDIUM / HIGH / EMERGENCY
    val followUpDate: Long?,
    val isResolved: Boolean = false,
    val notes: String?,
    val createdAt: Long,
    val syncStatus: String = "PENDING"
)

@Entity(tableName = "outbreak_alerts")
data class OutbreakAlertEntity(
    @PrimaryKey val alertId: String,
    val alertType: String,           // OUTBREAK / ADVISORY / CAMP / GOVERNMENT
    val disease: String,
    val severity: String,            // LOW / MEDIUM / HIGH / CRITICAL
    val affectedVillages: String,    // JSON array of village IDs
    val affectedSpecies: String,     // JSON array
    val title: String,
    val description: String,
    val actionRequired: String?,
    val reportedBy: String,
    val reportedByRole: String,
    val reportedAt: Long,
    val expiresAt: Long?,
    val isRead: Boolean = false,
    val isVerified: Boolean = false,
    val syncStatus: String = "PENDING"
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val firebaseUid: String,
    val name: String,
    val phone: String,
    val role: String,                // FARMER / VET / FIELD_OFFICER / ADMIN
    val villageId: String,
    val villageName: String,
    val district: String,
    val state: String = "Karnataka",
    val isVerified: Boolean = false,
    val profilePhotoUri: String?,
    val preferredLanguage: String = "en",  // en / kn
    val fcmToken: String?,
    val createdAt: Long,
    val lastLoginAt: Long
)

@Entity(tableName = "ai_cache")
data class AiCacheEntity(
    @PrimaryKey val cacheId: String,
    val queryHash: String,
    val query: String,
    val response: String,
    val cachedAt: Long,
    val expiresAt: Long
)
