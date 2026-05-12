package com.avivba.simbanyu.api.config

import com.avivba.simbanyu.api.components.CustomUserDetailsService
import com.avivba.simbanyu.api.config.properties.DemoProperties
import com.avivba.simbanyu.api.config.properties.FileStorageProperties
import com.avivba.simbanyu.api.config.properties.JwtProperties
import com.avivba.simbanyu.api.config.properties.MidtransProperties
import com.avivba.simbanyu.api.config.properties.PriceProperties
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.user.UserRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableConfigurationProperties(
    JwtProperties::class,
    PriceProperties::class,
    MidtransProperties::class,
    FileStorageProperties::class,
    DemoProperties::class
)
class Configuration {
    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(
        userDetailsService: CustomUserDetailsService
    ): DaoAuthenticationProvider {
        val authenticationProvider = DaoAuthenticationProvider(userDetailsService)
        authenticationProvider.setPasswordEncoder(encoder())
        return authenticationProvider
    }

    @Bean
    fun authenticationManager(
        config: AuthenticationConfiguration
    ): AuthenticationManager = config.authenticationManager
}