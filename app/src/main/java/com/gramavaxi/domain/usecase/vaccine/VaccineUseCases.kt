package com.gramavaxi.domain.usecase.vaccine

import com.gramavaxi.domain.model.VaccinationSchedule
import com.gramavaxi.domain.repository.VaccinationRepository
import com.gramavaxi.worker.WorkerScheduler
import javax.inject.Inject

class LogVaccinationUseCase @Inject constructor(
    private val repository: VaccinationRepository,
    private val workerScheduler: WorkerScheduler
) {
    suspend operator fun invoke(
        scheduleId: String,
        completedDate: Long,
        batchNumber: String?,
        notes: String?
    ): Result<Unit> {
        val result = repository.markVaccinationComplete(scheduleId, completedDate, batchNumber, notes)
        if (result.isSuccess) {
            // Cancel pending reminders for this dose
            workerScheduler.cancelVaccinationReminders(scheduleId)
        }
        return result
    }
}

class GetSchedulesForAnimalUseCase @Inject constructor(
    private val repository: VaccinationRepository
) {
    operator fun invoke(animalId: String) = repository.getSchedulesForAnimal(animalId)
}

class GetUpcomingVaccinationsUseCase @Inject constructor(
    private val repository: VaccinationRepository
) {
    operator fun invoke(daysAhead: Int = 7) = repository.getUpcomingVaccinations(daysAhead)
}

class GetOverdueVaccinationsUseCase @Inject constructor(
    private val repository: VaccinationRepository
) {
    suspend operator fun invoke() = repository.getOverdueVaccinations()
}

class ScheduleVaccineRemindersUseCase @Inject constructor(
    private val workerScheduler: WorkerScheduler
) {
    operator fun invoke(schedules: List<VaccinationSchedule>) {
        schedules.forEach { schedule ->
            workerScheduler.scheduleVaccinationReminders(schedule)
        }
    }
}
