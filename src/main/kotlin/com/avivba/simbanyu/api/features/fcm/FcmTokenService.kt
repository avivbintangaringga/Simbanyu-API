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
        val token = FcmToken(
            token = token,
            user = userRepository.findByUsername(username)
        )

        return fcmTokenRepository.save(token)
    }

    fun getUserToken(
        username: String
    ): String? {
        val token = fcmTokenRepository.findFcmTokenByUser_UsernameOrderByCreatedAtDesc(username)
        return if (token.isNotEmpty()) {
            token[0].token
        } else {
            null
        }
    }
}