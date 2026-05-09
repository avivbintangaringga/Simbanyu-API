package com.avivba.simbanyu.api.features.activity

import com.avivba.simbanyu.api.features.activity.model.Activity
import com.avivba.simbanyu.api.features.user.model.User
import com.avivba.simbanyu.api.features.user.model.UserRole
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
) {
    fun getAllActivities(user: User, limit: Int?): List<Activity> {
        val sort = Sort.by(Sort.Direction.DESC, "createdAt")
        val limit = limit?.let { Limit.of(it) } ?: Limit.unlimited()
        return if (user.role == UserRole.ADMIN) {
            activityRepository.findBy(
                sort,
                limit
            )
        } else {
            activityRepository.findActivitiesByUserId(
                user.id!!,
                sort,
                limit
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