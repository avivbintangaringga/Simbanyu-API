package com.avivba.simbanyu.api.features.user

import com.avivba.simbanyu.api.features.upload.UploadService
import com.avivba.simbanyu.api.features.upload.model.Upload
import com.avivba.simbanyu.api.features.user.model.AddUserRequest
import com.avivba.simbanyu.api.features.user.model.UpdateUserRequest
import com.avivba.simbanyu.api.features.user.model.User
import com.avivba.simbanyu.api.features.user.model.UserRole
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val uploadService: UploadService
) {
    fun getAllUsers(): List<User> = userRepository.findAll()

    fun getUserById(id: String): User =
        userRepository.findById(UUID.fromString(id)).orElseThrow()

    @Transactional
    fun addUser(data: AddUserRequest, image: MultipartFile?): User {
        var photoImage: Upload? = null

        if (image != null) {
            photoImage = uploadService.saveMultipart(image)
        }

        val user = User(
            username = data.username,
            password = encoder.encode(data.password)!!,
            name = data.name,
            phoneNumber = data.phoneNumber,
            photoImage = photoImage,
            role = UserRole.valueOf(data.role),
        )

        return userRepository.save(user)
    }

    fun updateUser(id: String, data: UpdateUserRequest, image: MultipartFile?): User {
        val user = userRepository
            .findById(UUID.fromString(id))
            .orElseThrow()

        data.name?.let { user.name = it }
        data.phoneNumber?.let { user.phoneNumber = it }
        data.password?.let { user.password = encoder.encode(it)!! }
        if (image != null) {
            if (user.photoImage != null) {
                uploadService.deleteFile(user.photoImage!!)
            }

            val photoImage = uploadService.saveMultipart(image)
            user.photoImage = photoImage
        }

        val updatedUser = userRepository.save(user)
        return updatedUser
    }

    fun deleteUser(id: String) = userRepository.deleteById(UUID.fromString(id))
}