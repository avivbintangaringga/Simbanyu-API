package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.FcmToken
import org.springframework.data.jpa.repository.JpaRepository

interface FcmTokenRepository : JpaRepository<FcmToken, String> {
}