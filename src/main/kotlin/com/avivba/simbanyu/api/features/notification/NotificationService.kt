package com.avivba.simbanyu.api.features.notification

import com.avivba.simbanyu.api.features.fcm.FcmTokenRepository
import com.avivba.simbanyu.api.features.user.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val fcmTokenRepository: FcmTokenRepository
) {
    fun sendBroadcastNotification(
        title: String,
        body: String,
    ) {
        // TODO: Implement broadcast notification
        println("Broadcast notification: $title - $body")
    }

    fun sendNotificationToUser(
        title: String,
        body: String,
        user: User
    ) {
        // TODO: Implement notification to user
        println("Notification to user (${user.username}): $title - $body")
    }

    fun getUserNotifications(
        user: User
    ): List<Notification> {
        return notificationRepository.findNotificationsByUser(user)
    }
}