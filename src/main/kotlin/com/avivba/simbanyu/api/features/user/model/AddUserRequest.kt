package com.avivba.simbanyu.api.features.user.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class AddUserRequest(
    @field:NotBlank
    @field:Length(min = 3, max = 255)
    val username: String,

    @field:NotBlank
    @field:Length(min = 3, max = 255)
    val name: String,

    @field:NotBlank
    @field:Length(min = 3, max = 255)
    val password: String,

    @field:NotBlank
    @field:Length(min = 9, max = 15)
    val phoneNumber: String,

    @field:NotBlank
    @field:Pattern(regexp = "(SUBSCRIBER|OPERATOR|ADMIN)")
    val role: String = UserRole.SUBSCRIBER.name,
)
