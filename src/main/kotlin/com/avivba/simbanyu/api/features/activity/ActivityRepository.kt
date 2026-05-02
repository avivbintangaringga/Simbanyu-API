package com.avivba.simbanyu.api.features.activity

import com.avivba.simbanyu.api.features.activity.model.Activity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ActivityRepository : JpaRepository<Activity, UUID> {
    fun findActivitiesByUserId(id: UUID): List<Activity>
}