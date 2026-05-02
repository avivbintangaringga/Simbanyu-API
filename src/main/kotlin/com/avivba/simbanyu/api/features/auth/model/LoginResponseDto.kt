package com.avivba.simbanyu.api.features.auth.model

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String
)
