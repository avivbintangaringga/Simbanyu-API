package com.avivba.simbanyu.api.features.plans

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.config.ResponseMessage
import com.avivba.simbanyu.api.features.plans.model.PlanDto
import com.avivba.simbanyu.api.features.plans.model.PlanRequest
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
        @Valid @RequestBody body: PlanRequest
    ): PlanDto {
        return planService.addPlan(body).let {
            PlanDto.fromEntity(it)
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Plan updated successfully.")
    fun updatePlan(
        @PathVariable id: String,
        @Valid @RequestBody body: PlanRequest
    ): PlanDto {
        return planService.updatePlan(id, body).let {
            PlanDto.fromEntity(it)
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Plan deleted successfully.")
    fun deletePlan(
        @PathVariable id: String
    ) {
        planService.deletePlan(id)
    }
}