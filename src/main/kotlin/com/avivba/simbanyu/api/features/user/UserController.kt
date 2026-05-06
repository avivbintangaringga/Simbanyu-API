package com.avivba.simbanyu.api.features.user

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.user.model.AddUserRequest
import com.avivba.simbanyu.api.features.user.model.UpdateUserRequest
import com.avivba.simbanyu.api.features.user.model.UserDto
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
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

    @GetMapping("/me")
    @PostAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or #returnObject.username == principal.username")
    @ResponseMessage("User retrieved successfully.")
    fun getMe(authentication: Authentication): UserDto {
        return userService.getUserByUsername(authentication.name)
            .let {
                UserDto.fromEntity(it)
            }
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or #returnObject.username == principal.username")
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
        @RequestPart("image") image: MultipartFile? = null,
        authentication: Authentication
    ): UserDto {
        return userService.addUser(
            data = data,
            image = image,
            username = authentication.name
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
        @RequestPart("image") image: MultipartFile? = null,
        authentication: Authentication
    ): UserDto {
        return userService.updateUser(
            id = id,
            data = data,
            image = image,
            username = authentication.name
        ).let {
            UserDto.fromEntity(it)
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("User deleted successfully.")
    fun deleteUser(
        @PathVariable id: String,
        authentication: Authentication
    ) {
        userService.deleteUser(
            id = id,
            username = authentication.name
        )
    }
}