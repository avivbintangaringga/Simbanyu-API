package com.avivba.simbanyu.api.features.activity

import com.avivba.simbanyu.api.features.activity.model.Activity
import com.avivba.simbanyu.api.features.user.model.User
import com.avivba.simbanyu.api.features.user.model.UserRole
import org.springframework.stereotype.Service

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
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