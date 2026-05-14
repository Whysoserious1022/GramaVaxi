package com.gramavaxi.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gramavaxi.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_VACCINATION = "vaccination_reminders"
        const val CHANNEL_ALERTS = "outbreak_alerts"
        const val CHANNEL_AI_TIPS = "ai_care_tips"
        const val CHANNEL_HEALTH = "health_checks"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        listOf(
            NotificationChannel(
                CHANNEL_VACCINATION,
                "Vaccination Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming livestock vaccinations"
                enableVibration(true)
            },
            NotificationChannel(
                CHANNEL_ALERTS,
                "Disease Outbreak Alerts",
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = "Urgent alerts about disease outbreaks in your area"
                enableVibration(true)
            },
            NotificationChannel(
                CHANNEL_AI_TIPS,
                "AI Care Tips",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Seasonal care tips from Grama-Vaxi AI"
            },
            NotificationChannel(
                CHANNEL_HEALTH,
                "Animal Health Checks",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to check animal health factors"
            }
        ).forEach { manager.createNotificationChannel(it) }
    }

    fun showVaccinationReminder(title: String, message: String, scheduleId: String) {
        val pendingIntent = appPendingIntent(scheduleId)

        val notification = NotificationCompat.Builder(context, CHANNEL_VACCINATION)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        show(scheduleId.hashCode(), notification)
    }

    fun showOutbreakAlert(title: String, message: String, alertId: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(appPendingIntent(alertId))
            .setAutoCancel(true)
            .build()

        show(alertId.hashCode(), notification)
    }

    fun showHealthCheckReminder(title: String, message: String, notificationId: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_HEALTH)
            .setSmallIcon(android.R.drawable.ic_menu_myplaces)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(appPendingIntent(notificationId))
            .setAutoCancel(true)
            .build()

        show(notificationId.hashCode(), notification)
    }

    private fun appPendingIntent(id: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_id", id)
        }

        return PendingIntent.getActivity(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun show(id: Int, notification: android.app.Notification) {
        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (e: SecurityException) {
            Timber.e(e, "Notification permission not granted")
        }
    }
}
