package com.avivba.simbanyu.api.features.notification

import com.avivba.simbanyu.api.features.fcm.FcmTokenService
import com.avivba.simbanyu.api.features.notification.model.Notification
import com.avivba.simbanyu.api.features.user.model.User
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
) {
    fun sendBroadcastNotification(
        title: String,
        body: String,
    ) {
        sendPushNotificationToTopic(title, body, "broadcast")
        println("Broadcast notification: $title - $body")
    }

    fun sendNotificationToUser(
        title: String,
        body: String,
        user: User
    ) {
        val token = user.fcmToken?.token ?: return
        sendPushNotification(title, body, token)
        println("Notification to user (${user.username}): $title - $body")
    }

    fun getUserNotifications(
        user: User
    ): List<Notification> {
        return notificationRepository.findNotificationsByUser(user)
    }

    private fun sendPushNotificationToTopic(
        title: String,
        body: String,
        topic: String
    ) {
        // TODO : Implement notification to topic
    }

    private fun sendPushNotification(
        title: String,
        body: String,
        token: String
    ) {
        // TODO: Implement notification to user
    }
}