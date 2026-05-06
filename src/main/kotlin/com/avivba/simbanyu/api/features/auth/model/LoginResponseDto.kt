package com.avivba.simbanyu.api.features.auth.model

import com.avivba.simbanyu.api.features.user.model.UserDto

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)
