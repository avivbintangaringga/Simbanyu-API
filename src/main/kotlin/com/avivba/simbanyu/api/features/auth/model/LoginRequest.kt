package com.avivba.simbanyu.api.features.auth.model

import jakarta.validation.constraints.NotEmpty

data class LoginRequest(
    @field:NotEmpty
    val username: String,

    @field:NotEmpty
    val password: String
)
