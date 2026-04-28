package com.avivba.simbanyu.api.features.announcement

import org.springframework.data.jpa.repository.JpaRepository

interface AnnouncementRepository : JpaRepository<Announcement, String> {
}