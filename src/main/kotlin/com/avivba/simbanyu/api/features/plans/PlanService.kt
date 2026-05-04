package com.avivba.simbanyu.api.features.plans

import com.avivba.simbanyu.api.features.activity.ActivityService
import com.avivba.simbanyu.api.features.plans.model.Plan
import com.avivba.simbanyu.api.features.plans.model.PlanRequest
import com.avivba.simbanyu.api.features.user.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID
import javax.print.attribute.standard.RequestingUserName

@Service
class PlanService(
    private val planRepository: PlanRepository,
    private val userRepository: UserRepository,
    private val activityService: ActivityService,
) {
    fun getAllPlans() = planRepository.findAll()

    fun getPlanById(
        id: String
    ): Plan = planRepository.findById(UUID.fromString(id)).orElseThrow()

    fun addPlan(
        data: PlanRequest,
        username: String,
    ): Plan {
        val user = userRepository.findByUsername(username)
        val plan = Plan(
            name = data.name,
            basePrice = data.basePrice,
        )
        activityService.recordActivity(
            user = user,
            title = "Tambah Plan",
            body = "Plan baru telah ditambahkan.",
        )
        return planRepository.save(plan)
    }

    fun updatePlan(
        id: String,
        data: PlanRequest,
        username: String,
    ): Plan {
        val user = userRepository.findByUsername(username)
        val plan = planRepository.findById(UUID.fromString(id)).orElseThrow()
        plan.name = data.name
        plan.basePrice = data.basePrice
        activityService.recordActivity(
            user = user,
            title = "Update Plan",
            body = "Plan ${id} telah diperbarui.",
        )
        return planRepository.save(plan)
    }

    fun deletePlan(id: String, username: String) {
        val user = userRepository.findByUsername(username)
        val plan = planRepository.findById(UUID.fromString(id)).orElseThrow()
        planRepository.delete(plan)
        activityService.recordActivity(
            user = user,
            title = "Hapus Plan",
            body = "Plan ${id} telah dihapus.",
        )
    }
}