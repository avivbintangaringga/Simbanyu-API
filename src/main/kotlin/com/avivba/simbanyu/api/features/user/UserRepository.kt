package com.avivba.simbanyu.api.features.user

import com.avivba.simbanyu.api.features.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findUserByUsernameOrPhoneNumber(username: String, phoneNumber: String): User?
    fun findByUsername(username: String): User
}