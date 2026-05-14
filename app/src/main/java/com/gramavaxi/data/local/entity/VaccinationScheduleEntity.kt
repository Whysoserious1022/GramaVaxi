package com.gramavaxi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vaccination_schedules",
    foreignKeys = [
        ForeignKey(
            entity = AnimalEntity::class,
            parentColumns = ["animalId"],
            childColumns = ["animalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["animalId"])]
)
data class VaccinationScheduleEntity(
    @PrimaryKey val scheduleId: String,
    val animalId: String,
    val vaccineName: String,
    val vaccineType: String,         // FMD / BQ / HS / PPR / BRUCELLA / ANTHRAX etc.
    val doseNumber: Int,
    val totalDoses: Int,
    val dueDate: Long,
    val completedDate: Long?,
    val batchNumber: String?,
    val administeredBy: String?,     // Vet name / self
    val administeredByRole: String?, // VET / FARMER / FIELD_OFFICER
    val vetId: String?,
    val photoUri: String?,           // Photo of vaccine vial
    val status: String,              // PENDING / COMPLETED / OVERDUE / SKIPPED
    val notes: String?,
    val nextDoseDate: Long?,
    val reminderJobId: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val syncStatus: String = "PENDING"
)
