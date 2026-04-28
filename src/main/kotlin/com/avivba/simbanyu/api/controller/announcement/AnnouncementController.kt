package com.avivba.simbanyu.api.controller.announcement

import com.avivba.simbanyu.api.database.entity.Announcement
import com.avivba.simbanyu.api.service.AnnouncementService
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
    fun getAllAnnouncements() =
        announcementService.getAllAnnouncements()

    @GetMapping("/{id}")
    fun getAnnouncementById(@PathVariable id: String): Announcement =
        announcementService.getAnnouncementById(id)

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @PostMapping
    fun addAnnouncement(
        @RequestBody body: AnnouncementRequest
    ) =
        announcementService.addAnnouncement(body)

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @PutMapping("/{id}")
    fun updateAnnouncement(
        @PathVariable id: String,
        @RequestBody body: AnnouncementRequest
    ) = announcementService
        .updateAnnouncement(
            id,
            body
        )

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @DeleteMapping("/{id}")
    fun deleteAnnouncement(@PathVariable id: String) =
        announcementService.deleteAnnouncementById(id)
}