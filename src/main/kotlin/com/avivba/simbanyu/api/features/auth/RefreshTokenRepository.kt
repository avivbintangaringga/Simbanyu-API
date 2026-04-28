package com.avivba.simbanyu.api.features.auth

import com.avivba.simbanyu.api.features.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {
    fun findFirstByToken(token: String): RefreshToken?
    fun deleteRefreshTokensByUser(user: User)
}