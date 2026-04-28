package com.avivba.simbanyu.api.features.auth

import jakarta.validation.constraints.NotEmpty

data class AuthRequest(
    @NotEmpty
    val username: String,

    @NotEmpty
    val password: String
)
