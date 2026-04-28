package com.avivba.simbanyu.api.config

import com.avivba.simbanyu.api.components.CustomUserDetailsService
import com.avivba.simbanyu.api.features.auth.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substringAfter("Bearer ")
            val username = tokenService.extractUsername(token)

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val user = userDetailsService.loadUserByUsername(username)

                if (tokenService.isValid(token, user)) {
                    val authToken = UsernamePasswordAuthenticationToken(user, null, user.authorities)
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }

                filterChain.doFilter(request, response)
                return
            }
        } else {
            filterChain.doFilter(request, response)
            return
        }
    }
}