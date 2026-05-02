package com.avivba.simbanyu.api.features.notification.model

data class NotificationDto(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun fromEntity(notification: Notification) = NotificationDto(
            id = notification.id.toString(),
            title = notification.title,
            body = notification.body,
            createdAt = notification.createdAt.toString(),
            updatedAt = notification.updatedAt.toString(),
        )
    }
}