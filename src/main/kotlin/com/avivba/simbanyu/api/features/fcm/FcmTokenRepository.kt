package com.avivba.simbanyu.api.features.fcm

import com.avivba.simbanyu.api.features.fcm.model.FcmToken
import com.avivba.simbanyu.api.features.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FcmTokenRepository : JpaRepository<FcmToken, UUID> {
    fun findFcmTokenByUser(user: User): MutableList<FcmToken>
}