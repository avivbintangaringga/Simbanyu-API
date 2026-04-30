package com.avivba.simbanyu.api.features.prices

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class PriceRequest(
    @NotBlank
    val name: String,

    @Min(1)
    val tierOrder: Int,

    @Min(1)
    val pricePerMeter: Double,

    val maxVolume: Int? = null,
)
