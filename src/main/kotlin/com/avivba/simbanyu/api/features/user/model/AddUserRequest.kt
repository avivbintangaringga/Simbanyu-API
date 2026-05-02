package com.avivba.simbanyu.api.features.user.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.URL

data class AddUserRequest(
    @NotBlank
    val username: String,

    @NotBlank
    val name: String,

    @NotBlank
    val password: String,

    @NotBlank
    val phoneNumber: String,

    @URL
    val photoImageURL: String? = null,

    @NotBlank
    @Pattern(regexp = "(SUBSCRIBER|OPERATOR|ADMIN)")
    val role: String = UserRole.SUBSCRIBER.name,
)
