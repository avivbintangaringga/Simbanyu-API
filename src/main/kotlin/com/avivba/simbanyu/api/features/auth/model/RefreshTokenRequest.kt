package com.avivba.simbanyu.api.features.auth.model

import jakarta.validation.constraints.NotEmpty

data class RefreshTokenRequest(
    @NotEmpty
    val token: String
)
