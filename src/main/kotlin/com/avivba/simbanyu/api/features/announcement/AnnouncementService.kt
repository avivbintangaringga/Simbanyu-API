package com.avivba.simbanyu.api.features.announcement

import com.avivba.simbanyu.api.base.ApiResponse
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AnnouncementService(
    private val announcementRepository: AnnouncementRepository,
) {
    fun getAllAnnouncements() =
        announcementRepository
            .findAll()
            .let {
                ApiResponse.success(it)
            }

    fun getAnnouncementById(id: String) =
        announcementRepository
            .findById(
                UUID.fromString(id)
            ).orElseThrow()
            .let {
                ApiResponse.success(it)
            }

    fun addAnnouncement(data: AnnouncementRequest) =
        announcementRepository
            .save(
                Announcement(
                    title = data.title,
                    body = data.body
                )
            ).let {
                ApiResponse.success(it)
            }

    fun updateAnnouncement(id: String, data: AnnouncementRequest): ApiResponse<Announcement> {
        val announcement = announcementRepository
            .findById(
                UUID.fromString(id)
            ).orElseThrow()

        announcement.title = data.title
        announcement.body = data.body

        return announcementRepository
            .save(announcement)
            .let { ApiResponse.success(it) }
    }

    fun deleteAnnouncementById(id: String) =
        announcementRepository.deleteById(UUID.fromString(id))
}