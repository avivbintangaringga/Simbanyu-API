package com.avivba.simbanyu.api.features.announcement

data class AnnouncementDto(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: String,
    val updatedAt: String,
)