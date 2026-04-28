package com.avivba.simbanyu.api.features.activity

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.user.User
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.features.user.UserRole
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository
) {
    fun getAllActivities(user: User): List<Activity> {
        return if (user.role == UserRole.ADMIN) {
            activityRepository.findAll()
        } else {
            activityRepository.findActivitiesByUserId(user.id!!)
        }
    }

    fun recordActivity(
        user: User,
        title: String,
        body: String
    ) {
        val activity = Activity(
            title = title,
            body = body,
            user = user
        )

        activityRepository.save(activity)
    }
}