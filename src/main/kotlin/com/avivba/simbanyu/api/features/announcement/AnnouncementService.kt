package com.avivba.simbanyu.api.features.announcement

import org.springframework.stereotype.Service

@Service
class AnnouncementService(
    private val announcementRepository: AnnouncementRepository,
) {
    fun getAllAnnouncements() =
        announcementRepository.findAll()

    fun getAnnouncementById(id: String) =
        announcementRepository.findById(id).orElseThrow()

    fun addAnnouncement(data: AnnouncementRequest): Announcement {
        return announcementRepository.save(
            Announcement(
                title = data.title,
                body = data.body
            )
        )
    }

    fun updateAnnouncement(id: String, data: AnnouncementRequest): Announcement {
        val announcement = announcementRepository.findById(id).orElseThrow()
        announcement.title = data.title
        announcement.body = data.body

        return announcementRepository.save(announcement)
    }

    fun deleteAnnouncementById(id: String) =
        announcementRepository.deleteById(id)
}