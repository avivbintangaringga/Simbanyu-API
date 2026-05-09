package com.avivba.simbanyu.api.features.activity

import com.avivba.simbanyu.api.features.activity.model.Activity
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ActivityRepository : JpaRepository<Activity, UUID> {
    fun findActivitiesByUserId(id: UUID, sort: Sort, limit: Limit): List<Activity>
    fun findBy(sort: Sort, limit: Limit): List<Activity>
}