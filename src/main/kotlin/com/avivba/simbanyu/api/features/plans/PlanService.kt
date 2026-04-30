package com.avivba.simbanyu.api.features.plans

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PlanService(
    private val planRepository: PlanRepository
) {
    fun getAllPlans() = planRepository.findAll()

    fun getPlanById(
        id: String
    ): Plan = planRepository.findById(UUID.fromString(id)).orElseThrow()

    fun addPlan(
        data: PlanRequest
    ): Plan {
        val plan = Plan(
            name = data.name,
            basePrice = data.basePrice,
        )
        return planRepository.save(plan)
    }

    fun updatePlan(
        id: String,
        data: PlanRequest
    ): Plan {
        val plan = planRepository.findById(UUID.fromString(id)).orElseThrow()
        plan.name = data.name
        plan.basePrice = data.basePrice
        return planRepository.save(plan)
    }

    fun deletePlan(id: String) = planRepository.deleteById(UUID.fromString(id))
}