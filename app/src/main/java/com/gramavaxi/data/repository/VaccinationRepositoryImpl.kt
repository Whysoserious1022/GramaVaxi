package com.gramavaxi.data.repository

import com.gramavaxi.data.local.dao.VaccinationDao
import com.gramavaxi.data.local.entity.VaccinationScheduleEntity
import com.gramavaxi.domain.model.VaccinationSchedule
import com.gramavaxi.domain.model.VaccineStatus
import com.gramavaxi.domain.repository.VaccinationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VaccinationRepositoryImpl @Inject constructor(
    private val vaccinationDao: VaccinationDao
) : VaccinationRepository {

    override suspend fun saveSchedules(schedules: List<VaccinationSchedule>): Result<Unit> {
        return try {
            vaccinationDao.insertSchedules(schedules.map { it.toEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSchedulesForAnimal(animalId: String): Flow<List<VaccinationSchedule>> {
        return vaccinationDao.getSchedulesForAnimal(animalId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getScheduleById(scheduleId: String): VaccinationSchedule? {
        return vaccinationDao.getScheduleById(scheduleId)?.toDomain()
    }

    override suspend fun markVaccinationComplete(
        scheduleId: String,
        completedDate: Long,
        batchNumber: String?,
        notes: String?
    ): Result<Unit> {
        return try {
            vaccinationDao.markAsCompleted(scheduleId, completedDate, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUpcomingVaccinations(daysAhead: Int): Flow<List<VaccinationSchedule>> {
        val now = System.currentTimeMillis()
        val future = now + TimeUnit.DAYS.toMillis(daysAhead.toLong())
        return vaccinationDao.getUpcomingVaccinations(now, future).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getOverdueVaccinations(): List<VaccinationSchedule> {
        return vaccinationDao.getOverdueSchedules(System.currentTimeMillis()).map { it.toDomain() }
    }

    override suspend fun syncPendingSchedules() {
        // TODO: Sync with Firestore
    }

    private fun VaccinationSchedule.toEntity() = VaccinationScheduleEntity(
        scheduleId = scheduleId,
        animalId = animalId,
        vaccineName = vaccineName,
        vaccineType = vaccineType,
        doseNumber = doseNumber,
        totalDoses = totalDoses,
        dueDate = dueDate,
        completedDate = completedDate,
        batchNumber = null,
        administeredBy = null,
        administeredByRole = null,
        vetId = null,
        photoUri = null,
        status = status.name,
        notes = notes,
        nextDoseDate = null,
        reminderJobId = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    private fun VaccinationScheduleEntity.toDomain() = VaccinationSchedule(
        scheduleId = scheduleId,
        animalId = animalId,
        animalName = "",  // populated from join if needed
        vaccineName = vaccineName,
        vaccineType = vaccineType,
        doseNumber = doseNumber,
        totalDoses = totalDoses,
        dueDate = dueDate,
        completedDate = completedDate,
        status = try { VaccineStatus.valueOf(status) } catch (e: Exception) { VaccineStatus.PENDING },
        notes = notes
    )
}
