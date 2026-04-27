package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {
}