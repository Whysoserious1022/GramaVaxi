package com.gramavaxi.data.local.dao

import androidx.room.*
import com.gramavaxi.data.local.entity.VaccinationScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccinationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: VaccinationScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<VaccinationScheduleEntity>)

    @Update
    suspend fun updateSchedule(schedule: VaccinationScheduleEntity)

    @Query("SELECT * FROM vaccination_schedules WHERE animalId = :animalId ORDER BY dueDate ASC")
    fun getSchedulesForAnimal(animalId: String): Flow<List<VaccinationScheduleEntity>>

    @Query("SELECT * FROM vaccination_schedules WHERE scheduleId = :scheduleId")
    suspend fun getScheduleById(scheduleId: String): VaccinationScheduleEntity?

    @Query("SELECT * FROM vaccination_schedules WHERE status = 'PENDING' AND dueDate <= :date")
    suspend fun getOverdueSchedules(date: Long): List<VaccinationScheduleEntity>

    @Query("SELECT * FROM vaccination_schedules WHERE status = 'PENDING' AND dueDate BETWEEN :startDate AND :endDate")
    fun getUpcomingVaccinations(startDate: Long, endDate: Long): Flow<List<VaccinationScheduleEntity>>

    @Query("UPDATE vaccination_schedules SET status = 'COMPLETED', completedDate = :completedDate, updatedAt = :updatedAt, syncStatus = 'PENDING' WHERE scheduleId = :scheduleId")
    suspend fun markAsCompleted(scheduleId: String, completedDate: Long, updatedAt: Long)

    @Query("UPDATE vaccination_schedules SET status = 'OVERDUE', updatedAt = :updatedAt WHERE status = 'PENDING' AND dueDate < :now")
    suspend fun markOverdueSchedules(now: Long, updatedAt: Long)

    @Query("SELECT * FROM vaccination_schedules WHERE syncStatus = 'PENDING'")
    suspend fun getUnsyncedSchedules(): List<VaccinationScheduleEntity>

    @Query("SELECT COUNT(*) FROM vaccination_schedules WHERE animalId = :animalId AND status = 'COMPLETED'")
    suspend fun getCompletedVaccineCount(animalId: String): Int

    @Query("SELECT COUNT(*) FROM vaccination_schedules WHERE animalId = :animalId AND status = 'PENDING'")
    suspend fun getPendingVaccineCount(animalId: String): Int

    @Query("DELETE FROM vaccination_schedules WHERE animalId = :animalId")
    suspend fun deleteSchedulesForAnimal(animalId: String)
}
