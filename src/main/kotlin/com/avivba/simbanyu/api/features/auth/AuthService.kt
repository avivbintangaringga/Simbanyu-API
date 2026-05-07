package com.avivba.simbanyu.api.features.auth

import com.avivba.simbanyu.api.components.CustomUserDetailsService
import com.avivba.simbanyu.api.config.properties.JwtProperties
import com.avivba.simbanyu.api.features.auth.model.LoginRequest
import com.avivba.simbanyu.api.features.auth.model.LoginResponseDto
import com.avivba.simbanyu.api.features.auth.model.RefreshToken
import com.avivba.simbanyu.api.features.auth.model.TokenResponseDto
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.features.user.model.UserDto
import jakarta.transaction.Transactional
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun authenticate(body: LoginRequest): LoginResponseDto {
        val token = UsernamePasswordAuthenticationToken(body.username, body.password)
        authManager.authenticate(token)

        val userDetails = userDetailsService.loadUserByUsername(body.username)
        val user = userRepository.findUserByUsernameOrPhoneNumber(body.username, body.username)
            ?: throw AccessDeniedException("User not found")

        val accessToken = generateAccessToken(userDetails)
        val refreshToken = generateRefreshToken(userDetails)

        // Delete all refresh tokens for the user
        refreshTokenRepository.deleteRefreshTokensByUser(user)

        // Save the new refresh token
        refreshTokenRepository.save(
            RefreshToken(
                token = refreshToken,
                user = user
            )
        )

        return LoginResponseDto(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = UserDto.fromEntity(user)
        )
    }

    fun refreshAccessToken(token: String): TokenResponseDto {
        val username = tokenService.extractUsername(token) ?: throw IllegalArgumentException("Invalid token")
        val userDetails = userDetailsService.loadUserByUsername(username)
        val refreshTokenUser = refreshTokenRepository.findFirstByToken(token)

        if (!tokenService.isExpired(token) && refreshTokenUser?.user?.username == username) {
            return TokenResponseDto(
                token = generateAccessToken(userDetails),
            )
        } else {
            throw AccessDeniedException("Invalid refresh token")
        }
    }

    private fun generateAccessToken(userDetails: UserDetails): String = tokenService.generate(
        userDetails, Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
    )

    private fun generateRefreshToken(userDetails: UserDetails): String = tokenService.generate(
        userDetails, Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
    )
}