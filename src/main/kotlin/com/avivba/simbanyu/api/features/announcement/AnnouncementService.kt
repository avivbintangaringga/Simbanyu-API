package com.avivba.simbanyu.api.features.announcement

import com.avivba.simbanyu.api.features.activity.ActivityService
import com.avivba.simbanyu.api.features.announcement.model.Announcement
import com.avivba.simbanyu.api.features.announcement.model.AnnouncementRequest
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.notification.model.BroadcastTopic
import com.avivba.simbanyu.api.features.user.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AnnouncementService(
    private val announcementRepository: AnnouncementRepository,
    private val notificationService: NotificationService,
    private val activityService: ActivityService,
    private val userRepository: UserRepository,
) {
    fun getAllAnnouncements(): List<Announcement> =
        announcementRepository
            .findAll()

    fun getAnnouncementById(id: String): Announcement =
        announcementRepository
            .findById(
                UUID.fromString(id)
            ).orElseThrow()

    fun addAnnouncement(data: AnnouncementRequest, username: String): Announcement {
        val user = userRepository.findByUsername(username)
        val announcement = announcementRepository
            .save(
                Announcement(
                    title = data.title,
                    body = data.body
                )
            )

        notificationService.sendBroadcastNotification(
            data.title,
            data.body,
            BroadcastTopic.ALL
        )

        activityService.recordActivity(
            user = user,
            title = "Tambah Pemberitahuan",
            body = "Pemberitahuan baru telah ditambahkan.",
        )

        return announcement
    }

    fun updateAnnouncement(id: String, data: AnnouncementRequest, username: String): Announcement {
        val user = userRepository.findByUsername(username)
        val announcement = announcementRepository
            .findById(
                UUID.fromString(id)
            ).orElseThrow()

        announcement.title = data.title
        announcement.body = data.body

        notificationService.sendBroadcastNotification(
            data.title,
            data.body,
            BroadcastTopic.ALL
        )

        activityService.recordActivity(
            user = user,
            title = "Update Pemberitahuan",
            body = "Pemberitahuan telah diperbarui.",
        )

        return announcementRepository
            .save(announcement)
    }

    fun deleteAnnouncementById(id: String, username: String) {
        val user = userRepository.findByUsername(username)
        val announcement = announcementRepository.findById(UUID.fromString(id)).orElseThrow()
        announcementRepository.delete(announcement)
        activityService.recordActivity(
            user = user,
            title = "Hapus Pemberitahuan",
            body = "Pemberitahuan telah dihapus.",
        )
    }
}