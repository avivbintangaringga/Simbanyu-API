package com.avivba.simbanyu.api.features.announcement

import com.avivba.simbanyu.api.base.ApiResponse
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
    fun getAllAnnouncements(): ApiResponse<List<AnnouncementDto>> {
        return announcementService.getAllAnnouncements()
            .let {
                ApiResponse.success(
                    "Announcements retrieved successfully.",
                    it.map { announcement ->
                        AnnouncementDto.fromEntity(announcement)
                    })
            }
    }

    @GetMapping("/{id}")
    fun getAnnouncementById(
        @PathVariable id: String
    ): ApiResponse<AnnouncementDto> =
        announcementService.getAnnouncementById(id)
            .let {
                ApiResponse.success(
                    "Announcement retrieved successfully.",
                    AnnouncementDto.fromEntity(it)
                )
            }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @PostMapping
    fun addAnnouncement(
        @Valid @RequestBody body: AnnouncementRequest
    ): ApiResponse<AnnouncementDto> =
        announcementService.addAnnouncement(body)
            .let {
                ApiResponse.success(
                    "Announcement created successfully.",
                    AnnouncementDto.fromEntity(it)
                )
            }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @PutMapping("/{id}")
    fun updateAnnouncement(
        @PathVariable id: String,
        @Valid @RequestBody body: AnnouncementRequest
    ): ApiResponse<AnnouncementDto> =
        announcementService
            .updateAnnouncement(
                id,
                body
            )
            .let {
                ApiResponse.success(
                    "Announcement updated successfully.",
                    AnnouncementDto.fromEntity(it)
                )
            }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @DeleteMapping("/{id}")
    fun deleteAnnouncement(@PathVariable id: String): ApiResponse<Unit> =
        announcementService.deleteAnnouncementById(id)
            .let {
                ApiResponse.success(
                    "Announcement deleted successfully.",
                    it
                )
            }
}