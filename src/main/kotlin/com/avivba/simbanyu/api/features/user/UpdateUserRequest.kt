package com.avivba.simbanyu.api.features.user

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class UpdateUserRequest(
    @NotBlank
    val name: String? = null,

    @NotBlank
    val password: String? = null,

    @NotBlank
    val phoneNumber: String? = null,

    @NotBlank
    @URL
    val photoImageURL: String? = null,
)
