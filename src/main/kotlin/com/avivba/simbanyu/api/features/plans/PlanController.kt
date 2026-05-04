package com.avivba.simbanyu.api.features.plans

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.plans.model.PlanDto
import com.avivba.simbanyu.api.features.plans.model.PlanRequest
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/plans")
class PlanController(
    private val planService: PlanService
) {
    @GetMapping
    @ResponseMessage("Plans retrieved successfully.")
    fun getAllPlans(): List<PlanDto> {
        return planService.getAllPlans().map {
            PlanDto.fromEntity(it)
        }
    }

    @GetMapping("/{id}")
    @ResponseMessage("Plan retrieved successfully.")
    fun getPlanById(
        @PathVariable id: String
    ): PlanDto {
        return planService.getPlanById(id).let {
            PlanDto.fromEntity(it)
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Plan created successfully.")
    fun addPlan(
        @Valid @RequestBody body: PlanRequest,
        authentication: Authentication
    ): PlanDto {
        return planService.addPlan(
            data = body,
            username = authentication.name
        ).let {
            PlanDto.fromEntity(it)
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Plan updated successfully.")
    fun updatePlan(
        @PathVariable id: String,
        @Valid @RequestBody body: PlanRequest,
        authentication: Authentication
    ): PlanDto {
        return planService.updatePlan(
            id = id,
            data = body,
            username = authentication.name
        ).let {
            PlanDto.fromEntity(it)
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Plan deleted successfully.")
    fun deletePlan(
        @PathVariable id: String,
        authentication: Authentication
    ) {
        planService.deletePlan(
            id = id,
            username = authentication.name
        )
    }
}