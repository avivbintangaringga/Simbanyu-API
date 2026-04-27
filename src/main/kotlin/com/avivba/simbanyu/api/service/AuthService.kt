package com.avivba.simbanyu.api.service

import com.avivba.simbanyu.api.config.JwtProperties
import com.avivba.simbanyu.api.controller.auth.AuthRequest
import com.avivba.simbanyu.api.controller.auth.AuthResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import java.util.Date

@Service
class AuthService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties
) {
    fun authenticate(body: AuthRequest): AuthResponse {
        val token = UsernamePasswordAuthenticationToken(body.username, body.password)
        authManager.authenticate(token)

        val user = userDetailsService.loadUserByUsername(body.username)
        val accessToken = tokenService.generate(
            user, Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )

        return AuthResponse(accessToken)
    }
}