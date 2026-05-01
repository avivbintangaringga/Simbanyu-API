package com.avivba.simbanyu.api.features.subscriber

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class AddSubscriberRequest(
    @NotBlank
    val name: String,

    @NotBlank
    val phoneNumber: String,

    @NotBlank
    val password: String,

    @NotBlank
    val meterId: String,

    @Min(0)
    val initialMeter: Int,

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
