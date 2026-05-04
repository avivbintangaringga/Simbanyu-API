package com.avivba.simbanyu.api.features.fcm

import com.avivba.simbanyu.api.features.fcm.model.FcmToken
import com.avivba.simbanyu.api.features.user.UserRepository
import org.springframework.stereotype.Service

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository,
    private val userRepository: UserRepository
) {
    fun submitToken(
        token: String,
        username: String
    ): FcmToken {
        val user = userRepository.findByUsername(username)
        val currentToken = fcmTokenRepository.findFcmTokenByUser(user).firstOrNull()

        if (currentToken != null) {
            currentToken.token = token
            return fcmTokenRepository.save(currentToken)
        }

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
}