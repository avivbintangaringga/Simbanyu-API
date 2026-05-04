package com.avivba.simbanyu.api.features.user

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.config.ResponseMessage
import com.avivba.simbanyu.api.features.user.model.AddUserRequest
import com.avivba.simbanyu.api.features.user.model.UpdateUserRequest
import com.avivba.simbanyu.api.features.user.model.UserDto
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Users retrieved successfully.")
    fun getAllUsers(): List<UserDto> {
        return userService.getAllUsers()
            .map { user ->
                UserDto.fromEntity(user)
            }
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or #returnObject.data.username == principal.username")
    @ResponseMessage("User retrieved successfully.")
    fun getUserById(
        @PathVariable id: String
    ): UserDto {
        return userService.getUserById(id)
            .let {
                UserDto.fromEntity(it)
            }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("User created successfully.")
    fun addUser(
        @Valid @RequestPart("data") data: AddUserRequest,
        @RequestPart("image") image: MultipartFile? = null
    ): UserDto {
        return userService.addUser(
            data = data,
            image = image
        ).let {
            UserDto.fromEntity(it)
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("User updated successfully.")
    fun updateUser(
        @PathVariable id: String,
        @Valid @RequestPart("data") data: UpdateUserRequest,
        @RequestPart("image") image: MultipartFile? = null
    ): UserDto {
        return userService.updateUser(
            id = id,
            data = data,
            image = image
        ).let {
            UserDto.fromEntity(it)
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("User deleted successfully.")
    fun deleteUser(
        @PathVariable id: String
    ) {
        userService.deleteUser(id)
    }
}