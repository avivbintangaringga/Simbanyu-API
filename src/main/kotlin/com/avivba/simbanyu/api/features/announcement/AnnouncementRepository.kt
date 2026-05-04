package com.avivba.simbanyu.api.features.announcement

import com.avivba.simbanyu.api.features.announcement.model.Announcement
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AnnouncementRepository : JpaRepository<Announcement, UUID> {
}