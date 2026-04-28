package com.avivba.simbanyu.api.features.notification

import com.avivba.simbanyu.api.features.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, String> {
    fun findNotificationsByUser(user: User): MutableList<Notification>
}