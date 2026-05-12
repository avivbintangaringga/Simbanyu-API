package com.avivba.simbanyu.api.features.plans

import com.avivba.simbanyu.api.features.plans.model.Plan
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PlanRepository : JpaRepository<Plan, UUID> {
    fun findPlanByName(name: String): MutableList<Plan>
}