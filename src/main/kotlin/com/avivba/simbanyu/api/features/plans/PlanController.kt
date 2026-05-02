package com.avivba.simbanyu.api.features.plans

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.plans.model.PlanDto
import com.avivba.simbanyu.api.features.plans.model.PlanRequest
import jakarta.validation.Valid
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
    fun getAllPlans(): ApiResponse<List<PlanDto>> {
        return planService.getAllPlans().map {
            PlanDto.fromEntity(it)
        }.let {
            ApiResponse.success(it)
        }
    }

    @GetMapping("/{id}")
    fun getPlanById(
        @PathVariable id: String
    ): ApiResponse<PlanDto> {
        return planService.getPlanById(id).let {
            ApiResponse.success(PlanDto.fromEntity(it))
        }
    }

    @PostMapping
    fun addPlan(
        @Valid @RequestBody body: PlanRequest
    ): ApiResponse<PlanDto> {
        return planService.addPlan(body).let {
            ApiResponse.success(PlanDto.fromEntity(it))
        }
    }

    @PutMapping("/{id}")
    fun updatePlan(
        @PathVariable id: String,
        @Valid @RequestBody body: PlanRequest
    ): ApiResponse<PlanDto> {
        return planService.updatePlan(id, body).let {
            ApiResponse.success(PlanDto.fromEntity(it))
        }
    }

    @DeleteMapping("/{id}")
    fun deletePlan(
        @PathVariable id: String
    ): ApiResponse<Unit> {
        return planService.deletePlan(id).let {
            ApiResponse.success(Unit)
        }
    }
}