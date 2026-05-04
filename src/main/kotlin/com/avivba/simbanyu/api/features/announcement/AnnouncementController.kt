package com.avivba.simbanyu.api.features.announcement

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.announcement.model.AnnouncementDto
import com.avivba.simbanyu.api.features.announcement.model.AnnouncementRequest
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/announcements")
class AnnouncementController(
    private val announcementService: AnnouncementService
) {
    @GetMapping
    @ResponseMessage("Announcements retrieved successfully.")
    fun getAllAnnouncements(): List<AnnouncementDto> {
        return announcementService.getAllAnnouncements()
            .let {
                it.map { announcement ->
                    AnnouncementDto.fromEntity(announcement)
                }
            }
    }

    @GetMapping("/{id}")
    @ResponseMessage("Announcement retrieved successfully.")
    fun getAnnouncementById(
        @PathVariable id: String
    ): AnnouncementDto =
        announcementService.getAnnouncementById(id)
            .let {
                AnnouncementDto.fromEntity(it)
            }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @ResponseMessage("Announcement created successfully.")
    fun addAnnouncement(
        @Valid @RequestBody body: AnnouncementRequest
    ): AnnouncementDto =
        announcementService.addAnnouncement(body)
            .let {
                AnnouncementDto.fromEntity(it)
            }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @ResponseMessage("Announcement updated successfully.")
    fun updateAnnouncement(
        @PathVariable id: String,
        @Valid @RequestBody body: AnnouncementRequest
    ): AnnouncementDto =
        announcementService
            .updateAnnouncement(
                id,
                body
            )
            .let {
                AnnouncementDto.fromEntity(it)
            }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @ResponseMessage("Announcement deleted successfully.")
    fun deleteAnnouncement(@PathVariable id: String) =
        announcementService.deleteAnnouncementById(id)
}