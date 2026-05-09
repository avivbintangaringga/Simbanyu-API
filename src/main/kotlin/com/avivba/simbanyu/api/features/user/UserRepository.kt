package com.avivba.simbanyu.api.features.user

import com.avivba.simbanyu.api.features.user.model.User
import com.avivba.simbanyu.api.features.user.model.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findUserByUsernameOrPhoneNumber(username: String, phoneNumber: String): User?
    fun findByUsername(username: String): User
    fun findAllByRole(role: UserRole): MutableList<User>
}