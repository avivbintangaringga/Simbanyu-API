package com.avivba.simbanyu.api.features.plans

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlanRepository : JpaRepository<Plan, UUID>