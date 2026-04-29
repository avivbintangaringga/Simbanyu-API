package com.avivba.simbanyu.api.features.subscriber

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AddSubscriberRequest(
    @NotBlank
    val meterId: String,

    @NotBlank
    val plan: String,

    @NotBlank
    val address: String,

    @NotBlank
    val longitude: Double,

    @NotBlank
    val latitude: Double,

    @NotBlank
    val userId: String,
)
