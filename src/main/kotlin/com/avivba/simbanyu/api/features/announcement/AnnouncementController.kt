package com.avivba.simbanyu.api.features.announcement

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.announcement.model.AnnouncementDto
import com.avivba.simbanyu.api.features.announcement.model.AnnouncementRequest
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/announcements")
class AnnouncementController(
    private val announcementService: AnnouncementService,
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
        @Valid @RequestBody body: AnnouncementRequest,
        authentication: Authentication
    ): AnnouncementDto {
        return announcementService.addAnnouncement(
            data = body,
            username = authentication.name
        )
            .let {
                AnnouncementDto.fromEntity(it)
            }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @ResponseMessage("Announcement updated successfully.")
    fun updateAnnouncement(
        @PathVariable id: String,
        @Valid @RequestBody body: AnnouncementRequest,
        authentication: Authentication
    ): AnnouncementDto =
        announcementService
            .updateAnnouncement(
                id = id,
                data = body,
                username = authentication.name
            )
            .let {
                AnnouncementDto.fromEntity(it)
            }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @ResponseMessage("Announcement deleted successfully.")
    fun deleteAnnouncement(
        @PathVariable id: String,
        authentication: Authentication
    ) = announcementService.deleteAnnouncementById(
        id = id,
        username = authentication.name
    )
}