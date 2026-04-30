package com.avivba.simbanyu.api.features.subscriber

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

    @NotBlank
    val planId: String,

    @NotBlank
    val address: String,

    @NotBlank
    val longitude: Double,

    @NotBlank
    val latitude: Double,
)
