package com.avivba.simbanyu.api.features.subscriber.model

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class UpdateSubscriberRequest(
    @field:NotBlank
    @field:Length(min = 3, max = 255)
    val meterId: String,

    @field:NotBlank
    @field:Length(min = 3, max = 255)
    val planId: String,

    @field:NotBlank
    @field:Length(min = 3, max = 255)
    val address: String,

    @field:Min(-180)
    @field:Max(180)
    val longitude: Double,

    @field:Min(-90)
    @field:Max(90)
    val latitude: Double,
)
