package com.avivba.simbanyu.api.features.auth

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String
)
