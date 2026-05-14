package com.gramavaxi.data.repository

import com.gramavaxi.data.local.dao.AnimalDao
import com.gramavaxi.data.local.entity.AnimalEntity
import com.gramavaxi.domain.model.Animal
import com.gramavaxi.domain.repository.AnimalRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class AnimalRepositoryImpl @Inject constructor(
    private val animalDao: AnimalDao,
    private val firestore: FirebaseFirestore
) : AnimalRepository {

    override suspend fun registerAnimal(animal: Animal): Result<Animal> {
        return try {
            val entity = animal.toEntity()
            animalDao.insertAnimal(entity)
            Result.success(animal)
        } catch (e: Exception) {
            Timber.e(e, "Failed to register animal")
            Result.failure(e)
        }
    }

    override suspend fun getAnimalById(animalId: String): Animal? {
        return animalDao.getAnimalById(animalId)?.toDomain()
    }

    override fun getAnimalsForOwner(ownerId: String): Flow<List<Animal>> {
        return animalDao.getAnimalsForOwner(ownerId).map { list -> list.map { it.toDomain() } }
    }

    override fun getAllAnimals(): Flow<List<Animal>> {
        return animalDao.getAllAnimals().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updateAnimal(animal: Animal): Result<Unit> {
        return try {
            animalDao.updateAnimal(animal.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAnimal(animalId: String): Result<Unit> {
        return try {
            val entity = animalDao.getAnimalById(animalId)
            entity?.let { animalDao.deleteAnimal(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncPendingAnimals() {
        try {
            val pending = animalDao.getUnsyncedAnimals()
            pending.forEach { entity ->
                firestore.collection("animals")
                    .document(entity.animalId)
                    .set(entity)
                    .addOnSuccessListener {
                        // Mark as synced in background
                    }
            }
        } catch (e: Exception) {
            Timber.e(e, "Sync failed")
        }
    }

    // Mappers
    private fun Animal.toEntity() = AnimalEntity(
        animalId = animalId,
        healthId = healthId,
        name = name,
        species = species,
        breed = breed,
        ageMonths = ageMonths,
        sex = sex,
        color = color,
        weight = weight,
        photoUri = photoUri,
        ownerId = ownerId,
        ownerName = ownerName,
        ownerPhone = ownerPhone,
        villageId = villageName.lowercase().replace(" ", "_"),
        villageName = villageName,
        district = district,
        gpsLat = gpsLat,
        gpsLng = gpsLng,
        notes = notes,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis(),
        syncStatus = "PENDING"
    )

    private fun AnimalEntity.toDomain() = Animal(
        animalId = animalId,
        healthId = healthId,
        name = name,
        species = species,
        breed = breed,
        ageMonths = ageMonths,
        sex = sex,
        color = color,
        weight = weight,
        photoUri = photoUri,
        ownerId = ownerId,
        ownerName = ownerName,
        ownerPhone = ownerPhone,
        villageName = villageName,
        district = district,
        gpsLat = gpsLat,
        gpsLng = gpsLng,
        notes = notes,
        createdAt = createdAt
    )
}
