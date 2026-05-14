package com.gramavaxi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val animalId: String,
    val healthId: String,           // e.g., AHID-2024-KA-001234
    val name: String,
    val species: String,            // Cow, Goat, Buffalo, Sheep, Pig
    val breed: String,
    val ageMonths: Int,
    val sex: String,                // Male / Female
    val color: String,
    val weight: Float?,
    val photoUri: String?,
    val ownerId: String,
    val ownerName: String,
    val ownerPhone: String,
    val villageId: String,
    val villageName: String,
    val district: String,
    val state: String = "Karnataka",
    val gpsLat: Double?,
    val gpsLng: Double?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val syncStatus: String = "PENDING"  // PENDING / SYNCED / CONFLICT
)
