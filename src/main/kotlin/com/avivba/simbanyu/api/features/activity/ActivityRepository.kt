package com.avivba.simbanyu.api.features.activity

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ActivityRepository : JpaRepository<Activity, UUID> {
    fun findActivitiesByUserId(id: UUID): List<Activity>
}