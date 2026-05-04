package com.avivba.simbanyu.api.features.prices.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class PriceRequest(
    @field:NotBlank
    val name: String,

    @field:Min(1)
    val tierOrder: Int,

    @field:Min(1)
    val pricePerMeter: Double,

    val maxVolume: Int? = null,
)
