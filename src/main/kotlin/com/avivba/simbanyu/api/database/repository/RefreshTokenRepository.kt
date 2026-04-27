package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.RefreshToken
import com.avivba.simbanyu.api.database.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {
    fun findFirstByToken(token: String): RefreshToken?
    fun deleteRefreshTokensByUser(user: User)
}