package com.avivba.simbanyu.api.features.subscriber

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class UpdateSubscriberRequest(
    @NotBlank
    val meterId: String,

    @NotBlank
    val planId: String,

    @NotBlank
    val address: String,

    @Min(-180)
    @Max(180)
    val longitude: Double,

    @Min(-90)
    @Max(90)
    val latitude: Double,
)
