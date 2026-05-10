package com.avivba.simbanyu.api.features.auth

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.auth.model.LoginRequest
import com.avivba.simbanyu.api.features.auth.model.RefreshTokenRequest
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
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
    @ResponseMessage("Login successful.")
    fun login(
        @Valid @RequestBody body: LoginRequest
    ) = authService.authenticate(body)

    @PostMapping("/refresh")
    @ResponseMessage("Access token refreshed successfully.")
    fun refresh(
        @Valid @RequestBody body: RefreshTokenRequest
    ) = authService.refreshAccessToken(body.token)

    @PostMapping("/logout")
    fun logout(
        authentication: Authentication
    ) = authService.logout(authentication.name)
}