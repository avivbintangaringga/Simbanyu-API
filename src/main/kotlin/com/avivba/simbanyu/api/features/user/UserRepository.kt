package com.avivba.simbanyu.api.features.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String> {
    fun findUserByUsernameOrPhoneNumber(username: String, phoneNumber: String): User?
    fun findByUsername(username: String): User
}