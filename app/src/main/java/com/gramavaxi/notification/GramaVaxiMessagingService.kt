package com.gramavaxi.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class GramaVaxiMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.d("FCM received: ${message.data}")

        val title = message.data["title"] ?: message.notification?.title ?: "Grama-Vaxi Alert"
        val body = message.data["body"] ?: message.notification?.body ?: ""
        val type = message.data["type"] ?: "GENERAL"
        val alertId = message.data["alert_id"] ?: "0"

        when (type) {
            "OUTBREAK_ALERT" -> notificationHelper.showOutbreakAlert(title, body, alertId)
            "VACCINATION_REMINDER" -> notificationHelper.showVaccinationReminder(title, body, alertId)
            else -> notificationHelper.showVaccinationReminder(title, body, alertId)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("New FCM token: $token")
        // TODO: Upload token to Firestore for this user
    }
}
