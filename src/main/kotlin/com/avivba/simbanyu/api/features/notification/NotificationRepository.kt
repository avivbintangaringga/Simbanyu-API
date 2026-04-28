package com.avivba.simbanyu.api.features.notification

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, String> {
}