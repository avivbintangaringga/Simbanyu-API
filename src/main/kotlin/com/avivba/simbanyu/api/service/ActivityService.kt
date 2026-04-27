package com.avivba.simbanyu.api.service

import com.avivba.simbanyu.api.database.entity.Activity
import com.avivba.simbanyu.api.database.repository.ActivityRepository
import com.avivba.simbanyu.api.database.repository.UserRepository
import com.jetbrains.exported.JBRApi
import org.springframework.http.HttpStatus
import org.springframework.security.config.web.server.ServerSecurityMarker
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.server.ResponseStatusException

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository
) {
    fun getAllActivities(): List<Activity> {
        val auth = SecurityContextHolder.getContext().authentication

        if (auth != null && auth.isAuthenticated) {
            val user = userRepository.findByUsername(auth.name)

            return if (auth.authorities.any { it.authority == "ROLE_ADMIN" }) {
                activityRepository.findAll()
            } else {
                activityRepository.findActivitiesByUserId(user.id!!)
            }
        } else {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User not authenticated")
        }
    }
}