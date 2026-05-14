package com.gramavaxi.worker

import android.content.Context
import androidx.work.*
import com.gramavaxi.domain.model.VaccinationSchedule
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerScheduler @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedules 4 reminders per vaccine dose: 7d, 3d, 1d, same day
     */
    fun scheduleVaccinationReminders(schedule: VaccinationSchedule) {
        val reminderDays = listOf(7, 3, 1, 0)
        val now = System.currentTimeMillis()

        reminderDays.forEach { daysBefore ->
            val rawDelayMs = schedule.dueDate - now - (daysBefore * 86_400_000L)
            if (rawDelayMs >= 0 || daysBefore == 0) {
                val delayMs = rawDelayMs.coerceAtLeast(0L)
                val inputData = workDataOf(
                    VaccinationReminderWorker.KEY_ANIMAL_NAME to schedule.animalName,
                    VaccinationReminderWorker.KEY_VACCINE_NAME to schedule.vaccineName,
                    VaccinationReminderWorker.KEY_SCHEDULE_ID to schedule.scheduleId,
                    VaccinationReminderWorker.KEY_DAYS_BEFORE to daysBefore
                )

                val request = OneTimeWorkRequestBuilder<VaccinationReminderWorker>()
                    .setInputData(inputData)
                    .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                    .addTag(reminderTag(schedule.scheduleId, daysBefore))
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiresBatteryNotLow(false)
                            .build()
                    )
                    .build()

                workManager.enqueueUniqueWork(
                    reminderTag(schedule.scheduleId, daysBefore),
                    ExistingWorkPolicy.REPLACE,
                    request
                )
            }
        }
    }

    fun cancelVaccinationReminders(scheduleId: String) {
        listOf(7, 3, 1, 0).forEach { daysBefore ->
            workManager.cancelUniqueWork(reminderTag(scheduleId, daysBefore))
        }
    }

    fun schedulePeriodicHealthCheck() {
        val request = PeriodicWorkRequestBuilder<HealthCheckWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(HealthCheckWorker.TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            HealthCheckWorker.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun schedulePeriodicSync() {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(SyncWorker.TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SyncWorker.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun reminderTag(scheduleId: String, daysBefore: Int) =
        "reminder_${scheduleId}_${daysBefore}d"
}
