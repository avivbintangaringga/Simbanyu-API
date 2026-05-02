package com.avivba.simbanyu.api.features.fcm

import com.avivba.simbanyu.api.features.fcm.model.FcmToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FcmTokenRepository : JpaRepository<FcmToken, UUID> {
    fun findFcmTokenByUser_UsernameOrderByCreatedAtDesc(username: String): List<FcmToken>
}