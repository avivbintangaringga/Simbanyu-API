package com.avivba.simbanyu.api.features.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder
) {
    fun getAllUsers(): List<User> = userRepository.findAll()

    fun getUserById(id: String): User =
        userRepository.findById(UUID.fromString(id)).orElseThrow()

    fun addUser(addUserRequest: AddUserRequest): User {
        val user = User(
            name = addUserRequest.name,
            phoneNumber = addUserRequest.phoneNumber,
            photoImageURL = addUserRequest.photoImageURL,
            password = encoder.encode(addUserRequest.password)!!
        )

        return userRepository.save(user)
    }

    fun updateUser(id: String, data: UpdateUserRequest): User {
        val user = userRepository
            .findById(UUID.fromString(id))
            .orElseThrow()

        data.name?.let { user.name = it }
        data.phoneNumber?.let { user.phoneNumber = it }
        data.photoImageURL?.let { user.photoImageURL = it }
        data.password?.let { user.password = encoder.encode(it)!! }

        val updatedUser = userRepository.save(user)
        return updatedUser
    }

    fun deleteUser(id: String) = userRepository.deleteById(UUID.fromString(id))
}