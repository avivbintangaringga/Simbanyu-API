package com.avivba.simbanyu.api.features.announcement.model

data class AnnouncementDto(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun fromEntity(announcement: Announcement) = AnnouncementDto(
            id = announcement.id.toString(),
            title = announcement.title,
            body = announcement.body,
            createdAt = announcement.createdAt.toString(),
            updatedAt = announcement.updatedAt.toString(),
        )
    }
}