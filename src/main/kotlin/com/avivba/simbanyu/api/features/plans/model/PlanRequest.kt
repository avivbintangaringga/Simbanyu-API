package com.avivba.simbanyu.api.features.plans.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class PlanRequest(
    @field:NotBlank
    val name: String,

    @field:Min(1)
    val basePrice: Double,
)
