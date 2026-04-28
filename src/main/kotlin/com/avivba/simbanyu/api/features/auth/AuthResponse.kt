package com.avivba.simbanyu.api.features.auth

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)
