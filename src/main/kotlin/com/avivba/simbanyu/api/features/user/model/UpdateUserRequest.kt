package com.avivba.simbanyu.api.features.user.model

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.URL

data class UpdateUserRequest(
    @field:NotBlank
    @field:Length(min = 3, max = 255)
    val name: String? = null,

    @field:NotBlank
    @field:Length(min = 3, max = 255)
    val password: String? = null,

    @field:NotBlank
    @field:Length(min = 9, max = 15)
    val phoneNumber: String? = null,
)
