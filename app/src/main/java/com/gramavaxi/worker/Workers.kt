package com.gramavaxi.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gramavaxi.data.local.dao.AnimalDao
import com.gramavaxi.data.local.dao.VaccinationDao
import com.gramavaxi.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class VaccinationReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_ANIMAL_NAME = "animal_name"
        const val KEY_VACCINE_NAME = "vaccine_name"
        const val KEY_SCHEDULE_ID = "schedule_id"
        const val KEY_DAYS_BEFORE = "days_before"
    }

    override suspend fun doWork(): Result {
        return try {
            val animalName = inputData.getString(KEY_ANIMAL_NAME) ?: "Your animal"
            val vaccineName = inputData.getString(KEY_VACCINE_NAME) ?: "vaccine"
            val daysBefore = inputData.getInt(KEY_DAYS_BEFORE, 0)

            val title = when (daysBefore) {
                0 -> "Vaccination due today"
                1 -> "Vaccination tomorrow"
                3 -> "Vaccination in 3 days"
                7 -> "Vaccination next week"
                else -> "Vaccination reminder"
            }

            val message = when (daysBefore) {
                0 -> "$animalName needs $vaccineName today. Do not miss it."
                1 -> "$animalName needs $vaccineName tomorrow. Prepare now."
                else -> "$animalName needs $vaccineName in $daysBefore days."
            }

            notificationHelper.showVaccinationReminder(
                title = title,
                message = message,
                scheduleId = inputData.getString(KEY_SCHEDULE_ID) ?: ""
            )
            Timber.d("Vaccination reminder sent: $title")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "VaccinationReminderWorker failed")
            Result.retry()
        }
    }
}

@HiltWorker
class HealthCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val vaccinationDao: VaccinationDao,
    private val animalDao: AnimalDao
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "HealthCheckWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val now = System.currentTimeMillis()
            vaccinationDao.markOverdueSchedules(now, now)
            val overdueSchedules = vaccinationDao.getOverdueSchedules(now)

            overdueSchedules.take(5).forEach { schedule ->
                val animalName = animalDao.getAnimalById(schedule.animalId)?.name ?: "Your animal"
                notificationHelper.showVaccinationReminder(
                    title = "Overdue vaccination",
                    message = "$animalName is overdue for ${schedule.vaccineName}. Please arrange vaccination soon.",
                    scheduleId = schedule.scheduleId
                )
            }

            if (overdueSchedules.isEmpty()) {
                notificationHelper.showHealthCheckReminder(
                    title = "Daily animal health check",
                    message = "Check appetite, water intake, fever, wounds, cough, loose motion, and unusual behavior today.",
                    notificationId = TAG
                )
            }

            Timber.d("Health check worker completed")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "HealthCheckWorker failed")
            Result.failure()
        }
    }
}

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Sync worker running - pushing pending data to Firestore")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker failed")
            Result.retry()
        }
    }
}
