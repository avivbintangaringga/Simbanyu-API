package com.avivba.simbanyu.api.config

import com.avivba.simbanyu.api.base.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider,
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain = http
        .csrf { it.disable() }
        .authorizeHttpRequests {
            it
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/refresh",
                    "/error",
                    "/uploads/**",
                    "/.well-known/**",
                ).permitAll()
                .anyRequest()
                .fullyAuthenticated()
        }
        .exceptionHandling {
            it.authenticationEntryPoint { _, response, authException ->
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.contentType = MediaType.APPLICATION_JSON_VALUE

                objectMapper.writeValue(
                    response.outputStream,
                    ApiResponse.error<Nothing>(
                        message = "Authentication failed",
                        errors = listOf(
                            authException.message ?: "Missing or invalid authentication credentials"
                        )
                    )
                )
            }
        }
        .sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter::class.java
        )
        .build()
}