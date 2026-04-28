package com.avivba.simbanyu.api.features.announcement

import com.avivba.simbanyu.api.features.notification.NotificationService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AnnouncementService(
    private val announcementRepository: AnnouncementRepository,
    private val notificationService: NotificationService
) {
    fun getAllAnnouncements(): List<Announcement> =
        announcementRepository
            .findAll()

    fun getAnnouncementById(id: String): Announcement =
        announcementRepository
            .findById(
                UUID.fromString(id)
            ).orElseThrow()

    fun addAnnouncement(data: AddAnnouncementRequest): Announcement {
        val announcement = announcementRepository
            .save(
                Announcement(
                    title = data.title,
                    body = data.body
                )
            )

        notificationService.sendBroadcastNotification(
            data.title,
            data.body
        )

        return announcement
    }

    fun updateAnnouncement(id: String, data: AddAnnouncementRequest): Announcement {
        val announcement = announcementRepository
            .findById(
                UUID.fromString(id)
            ).orElseThrow()

        announcement.title = data.title
        announcement.body = data.body

        notificationService.sendBroadcastNotification(
            data.title,
            data.body
        )

        return announcementRepository
            .save(announcement)
    }

    fun deleteAnnouncementById(id: String) =
        announcementRepository.deleteById(UUID.fromString(id))
}