package com.gramavaxi.data.local.dao

import androidx.room.*
import com.gramavaxi.data.local.entity.AnimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: AnimalEntity)

    @Update
    suspend fun updateAnimal(animal: AnimalEntity)

    @Delete
    suspend fun deleteAnimal(animal: AnimalEntity)

    @Query("SELECT * FROM animals WHERE animalId = :animalId")
    suspend fun getAnimalById(animalId: String): AnimalEntity?

    @Query("SELECT * FROM animals WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getAnimalsForOwner(ownerId: String): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals ORDER BY createdAt DESC")
    fun getAllAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE villageId = :villageId")
    fun getAnimalsByVillage(villageId: String): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE syncStatus = 'PENDING'")
    suspend fun getUnsyncedAnimals(): List<AnimalEntity>

    @Query("UPDATE animals SET syncStatus = 'SYNCED' WHERE animalId = :animalId")
    suspend fun markAsSynced(animalId: String)

    @Query("SELECT COUNT(*) FROM animals WHERE ownerId = :ownerId")
    suspend fun getAnimalCountForOwner(ownerId: String): Int

    @Query("SELECT * FROM animals WHERE species = :species AND ownerId = :ownerId")
    fun getAnimalsBySpecies(species: String, ownerId: String): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE healthId = :healthId")
    suspend fun getAnimalByHealthId(healthId: String): AnimalEntity?
}
