package com.gramavaxi.domain.repository

import com.gramavaxi.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    suspend fun registerAnimal(animal: Animal): Result<Animal>
    suspend fun getAnimalById(animalId: String): Animal?
    fun getAnimalsForOwner(ownerId: String): Flow<List<Animal>>
    fun getAllAnimals(): Flow<List<Animal>>
    suspend fun updateAnimal(animal: Animal): Result<Unit>
    suspend fun deleteAnimal(animalId: String): Result<Unit>
    suspend fun syncPendingAnimals()
}

interface VaccinationRepository {
    suspend fun saveSchedules(schedules: List<VaccinationSchedule>): Result<Unit>
    fun getSchedulesForAnimal(animalId: String): Flow<List<VaccinationSchedule>>
    suspend fun getScheduleById(scheduleId: String): VaccinationSchedule?
    suspend fun markVaccinationComplete(scheduleId: String, completedDate: Long, batchNumber: String?, notes: String?): Result<Unit>
    fun getUpcomingVaccinations(daysAhead: Int): Flow<List<VaccinationSchedule>>
    suspend fun getOverdueVaccinations(): List<VaccinationSchedule>
    suspend fun syncPendingSchedules()
}

interface AlertRepository {
    fun getAllAlerts(): Flow<List<OutbreakAlert>>
    fun getUnreadAlerts(): Flow<List<OutbreakAlert>>
    fun getUnreadAlertCount(): Flow<Int>
    suspend fun reportOutbreak(alert: OutbreakAlert): Result<Unit>
    suspend fun markAlertAsRead(alertId: String)
    suspend fun markAllAlertsAsRead()
    suspend fun syncAlerts()
}

interface AIRepository {
    suspend fun sendChatMessage(message: String): Result<String>
    suspend fun analyzeSymptoms(symptoms: String, species: String, ageMonths: Int): Result<DiseaseAnalysis>
    suspend fun analyzeAnimalImage(imageBase64: String, symptoms: String): Result<String>
    suspend fun generateCareTips(species: String, breed: String, season: String): Result<String>
    suspend fun getCachedOrFetch(query: String): Result<String>
}
