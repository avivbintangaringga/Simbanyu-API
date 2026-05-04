package com.avivba.simbanyu.api.features.fcm

import com.avivba.simbanyu.api.features.fcm.model.FcmToken
import com.avivba.simbanyu.api.features.notification.model.BroadcastTopic
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.features.user.model.UserRole
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.stereotype.Service

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository,
    private val userRepository: UserRepository,
    private val firebaseMessaging: FirebaseMessaging
) {
    fun submitToken(
        token: String,
        username: String
    ): FcmToken {
        val user = userRepository.findByUsername(username)
        val currentToken = fcmTokenRepository.findFcmTokenByUser(user).firstOrNull()

        if (currentToken != null) {
            val previousToken = currentToken.token
            unsubscribeTokenByRole(user.role, previousToken)
            currentToken.token = token
            subscribeTokenByRole(user.role, token)
            return fcmTokenRepository.save(currentToken)
        }

        subscribeTokenByRole(user.role, token)

        val token = FcmToken(
            token = token,
            user = user
        )

        return fcmTokenRepository.save(token)
    }

    fun getUserToken(
        username: String
    ): String? {
        val user = userRepository.findByUsername(username)
        val token = fcmTokenRepository.findFcmTokenByUser(user)
        return if (token.isNotEmpty()) {
            token[0].token
        } else {
            null
        }
    }

    private fun subscribeTokenByRole(
        role: UserRole,
        token: String,
    ) {
        subscribeToTopic(token, BroadcastTopic.ALL.topic)

        when (role) {
            UserRole.ADMIN -> {
                subscribeToTopic(token, BroadcastTopic.ADMIN.topic)
            }

            UserRole.OPERATOR -> {
                subscribeToTopic(token, BroadcastTopic.OPERATOR.topic)
            }

            UserRole.SUBSCRIBER -> {
                subscribeToTopic(token, BroadcastTopic.SUBSCRIBER.topic)
            }
        }
    }

    private fun unsubscribeTokenByRole(
        role: UserRole,
        token: String,
    ) {
        unsubscribeFromTopic(token, BroadcastTopic.ALL.topic)

        when (role) {
            UserRole.ADMIN -> {
                unsubscribeFromTopic(token, BroadcastTopic.ADMIN.topic)
            }

            UserRole.OPERATOR -> {
                unsubscribeFromTopic(token, BroadcastTopic.OPERATOR.topic)
            }

            UserRole.SUBSCRIBER -> {
                unsubscribeFromTopic(token, BroadcastTopic.SUBSCRIBER.topic)
            }
        }
    }

    private fun subscribeToTopic(
        token: String,
        topic: String
    ) {
        firebaseMessaging.subscribeToTopic(listOf(token), topic)
    }

    private fun unsubscribeFromTopic(
        token: String,
        topic: String
    ) {
        firebaseMessaging.unsubscribeFromTopic(listOf(token), topic)
    }
}