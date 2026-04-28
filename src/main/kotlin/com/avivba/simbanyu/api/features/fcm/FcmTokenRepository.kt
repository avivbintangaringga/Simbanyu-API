package com.avivba.simbanyu.api.features.fcm

import org.springframework.data.jpa.repository.JpaRepository

interface FcmTokenRepository : JpaRepository<FcmToken, String> {
}