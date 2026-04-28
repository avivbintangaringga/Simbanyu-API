package com.avivba.simbanyu.api.features.notification

data class NotificationDto(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: String,
    val updatedAt: String,
)