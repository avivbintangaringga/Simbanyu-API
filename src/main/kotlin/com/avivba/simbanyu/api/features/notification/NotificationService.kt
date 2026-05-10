package com.avivba.simbanyu.api.features.notification

import com.avivba.simbanyu.api.features.notification.model.BroadcastTopic
import com.avivba.simbanyu.api.features.notification.model.Notification
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.features.user.model.User
import com.avivba.simbanyu.api.features.user.model.UserRole
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import jakarta.transaction.Transactional
import org.springframework.data.domain.Limit
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val firebaseMessaging: FirebaseMessaging,
) {
    @Transactional
    fun sendBroadcastNotification(
        title: String,
        body: String,
        topic: BroadcastTopic
    ) {
        sendPushNotificationToTopic(title, body, topic.topic)

        val users = when (topic) {
            BroadcastTopic.ALL -> {
                userRepository.findAll()
            }

            BroadcastTopic.ADMIN -> {
                userRepository.findAllByRole(UserRole.ADMIN)
            }

            BroadcastTopic.OPERATOR -> {
                userRepository.findAllByRole(UserRole.OPERATOR)
            }

            BroadcastTopic.SUBSCRIBER -> {
                userRepository.findAllByRole(UserRole.SUBSCRIBER)
            }
        }

        val notifications = users.map { user ->
            Notification(title = title, body = body, user = user)
        }
        notificationRepository.saveAll(notifications)

        println("Broadcast notification: $title - $body")

    }

    fun sendNotificationToUser(
        title: String,
        body: String,
        user: User
    ) {
        notificationRepository.save(Notification(title = title, body = body, user = user))

        val token = user.fcmToken?.token ?: return
        sendPushNotification(title, body, token)
        println("Notification to user (${user.username}): $title - $body")
    }

    fun getUserNotifications(
        user: User
    ): List<Notification> {
        return notificationRepository.findNotificationsByUser(user, Limit.of(20))
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