package com.avivba.simbanyu.api.features.auth

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody body: AuthRequest
    ) = authService.authenticate(body)

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody body: RefreshTokenRequest
    ) = authService.refreshAccessToken(body.token)
}