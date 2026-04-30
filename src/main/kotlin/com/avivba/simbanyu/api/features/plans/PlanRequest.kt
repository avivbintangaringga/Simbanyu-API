package com.avivba.simbanyu.api.features.plans

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class PlanRequest(
    @NotBlank
    val name: String,

    @Min(1)
    val basePrice: Double,
)
