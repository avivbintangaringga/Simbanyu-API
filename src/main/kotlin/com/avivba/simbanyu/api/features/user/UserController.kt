package com.avivba.simbanyu.api.features.user

import com.avivba.simbanyu.api.base.ApiResponse
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUsers(): ApiResponse<List<UserDto>> {
        return userService.getAllUsers()
            .let {
                ApiResponse.success(
                    it.map { user ->
                        UserDto.fromEntity(user)
                    }
                )
            }
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or #returnObject.data.username == principal.username")
    fun getUserById(
        @PathVariable id: String
    ): ApiResponse<UserDto> {
        return userService.getUserById(id)
            .let { ApiResponse.success(UserDto.fromEntity(it)) }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun addUser(
        @Valid @RequestBody body: AddUserRequest
    ): ApiResponse<UserDto> {
        return userService.addUser(body)
            .let { ApiResponse.success(UserDto.fromEntity(it)) }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateUser(
        @PathVariable id: String,
        @Valid @RequestBody body: UpdateUserRequest
    ): ApiResponse<UserDto> {
        return userService.updateUser(id, body)
            .let { ApiResponse.success(UserDto.fromEntity(it)) }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(
        @PathVariable id: String
    ): ApiResponse<Unit> {
        userService.deleteUser(id)
        return ApiResponse.success(Unit)
    }
}