package com.avivba.simbanyu.api

import com.avivba.simbanyu.api.features.user.User
import com.avivba.simbanyu.api.features.user.UserRole
import com.avivba.simbanyu.api.features.user.UserRepository
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

            userRepository.save(
                User(
                    username = "operator",
                    password = encoder.encode("operator")!!,
                    name = "Operator",
                    role = UserRole.OPERATOR,
                    phoneNumber = "08111111111",
                    photoImageURL = "https://example.com/photo.jpg"
                )
            )

            userRepository.save(
                User(
                    username = "user",
                    password = encoder.encode("user")!!,
                    name = "User",
                    role = UserRole.SUBSCRIBER,
                    phoneNumber = "0822222222",
                    photoImageURL = "https://example.com/photo.jpg"
                )
            )
        }
    }
}