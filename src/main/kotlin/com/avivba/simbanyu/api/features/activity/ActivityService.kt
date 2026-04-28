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
    fun getAllActivities(user: UserDetails): ApiResponse<List<Activity>> {
        val user = userRepository.findByUsername(user.username)

        return if (user.role == UserRole.ADMIN) {
            ApiResponse.success(
                activityRepository.findAll()
            )
        } else {
            ApiResponse.success(
                activityRepository.findActivitiesByUserId(user.id!!)
            )
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