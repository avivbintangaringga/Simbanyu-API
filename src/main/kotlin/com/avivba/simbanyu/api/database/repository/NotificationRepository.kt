package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, String> {
}