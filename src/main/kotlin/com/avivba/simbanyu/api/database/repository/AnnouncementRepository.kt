package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.Announcement
import org.springframework.data.jpa.repository.JpaRepository

interface AnnouncementRepository : JpaRepository<Announcement, String> {
}