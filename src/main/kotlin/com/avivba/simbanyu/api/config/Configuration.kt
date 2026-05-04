package com.avivba.simbanyu.api.config

import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.components.CustomUserDetailsService
import com.avivba.simbanyu.api.config.properties.FileStorageProperties
import com.avivba.simbanyu.api.config.properties.JwtProperties
import com.avivba.simbanyu.api.config.properties.MidtransProperties
import com.avivba.simbanyu.api.config.properties.PriceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
)
class Configuration {
    @Bean
    fun userDetailsService(
        userRepository: UserRepository,
        subscriberRepository: SubscriberRepository
    ): UserDetailsService = CustomUserDetailsService(
        userRepository,
        subscriberRepository
    )

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(
        userRepository: UserRepository,
        subscriberRepository: SubscriberRepository
    ): AuthenticationProvider = DaoAuthenticationProvider(
        userDetailsService(
            userRepository,
            subscriberRepository
        )
    ).also {
        it.setPasswordEncoder(encoder())
    }

    @Bean
    fun authenticationManager(
        config: AuthenticationConfiguration
    ): AuthenticationManager = config.authenticationManager
}