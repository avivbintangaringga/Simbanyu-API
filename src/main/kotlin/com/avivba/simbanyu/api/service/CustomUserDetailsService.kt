package com.avivba.simbanyu.api.service

import com.avivba.simbanyu.api.database.repository.SubscriberRepository
import com.avivba.simbanyu.api.database.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

typealias AppUser = com.avivba.simbanyu.api.database.entity.User

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
    private val subscriberRepository: SubscriberRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        var user = userRepository.findUserByUsernameOrPhoneNumber(username, username)
        if (user == null) {
            val subscriber = subscriberRepository.findSubscriberByMeterId(username)
            user = subscriber?.user
        }

        if (user != null) {
            return user.toUserDetails()
        } else {
            throw UsernameNotFoundException("User not found")
        }
    }

    private fun AppUser.toUserDetails(): UserDetails =
        User.builder()
            .username(this.username)
            .password(this.password)
            .roles(this.role.name)
            .build()
}