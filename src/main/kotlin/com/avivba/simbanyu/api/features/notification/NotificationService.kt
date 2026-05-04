package com.avivba.simbanyu.api.features.notification

import com.avivba.simbanyu.api.features.notification.model.BroadcastTopic
import com.avivba.simbanyu.api.features.notification.model.Notification
import com.avivba.simbanyu.api.features.user.model.User
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val firebaseMessaging: FirebaseMessaging,
) {
    fun sendBroadcastNotification(
        title: String,
        body: String,
        topic: BroadcastTopic
    ) {
        sendPushNotificationToTopic(title, body, topic.topic)
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
        val message = Message.builder()
            .putData("title", title)
            .putData("body", body)
            .setTopic(topic)
            .build()

        val response = firebaseMessaging.send(message)
        println("Successfully sent topic message: $response")
    }

    private fun sendPushNotification(
        title: String,
        body: String,
        token: String
    ) {
        val message = Message.builder()
            .setToken(token)
            .putData("title", title)
            .putData("body", body)
            .build()

        val response = firebaseMessaging.send(message)
        println("Successfully sent message: $response")
    }
}