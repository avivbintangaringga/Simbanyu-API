package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.Activity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ActivityRepository : JpaRepository<Activity, UUID> {
    fun findActivitiesByUserId(id: UUID): List<Activity>
}