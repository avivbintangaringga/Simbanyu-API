package com.avivba.simbanyu.api.features.auth

import jakarta.validation.constraints.NotEmpty

data class RefreshTokenRequest(
    @NotEmpty
    val token: String
)
