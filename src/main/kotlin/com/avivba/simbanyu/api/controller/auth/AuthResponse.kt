package com.avivba.simbanyu.api.controller.auth

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)
