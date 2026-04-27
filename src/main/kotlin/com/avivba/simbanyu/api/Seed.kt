package com.avivba.simbanyu.api

import com.avivba.simbanyu.api.database.entity.User
import com.avivba.simbanyu.api.database.entity.UserRole
import com.avivba.simbanyu.api.database.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class Seed(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder
) : CommandLineRunner {
    override fun run(vararg args: String) {
        if (userRepository.count() == 0L) {
            userRepository.save(
                User(
                    username = "admin",
                    password = encoder.encode("admin")!!,
                    name = "Admin",
                    role = UserRole.ADMIN,
                    phoneNumber = "08123456789",
                    photoImageURL = "https://example.com/photo.jpg"
                )
            )
        }
    }
}