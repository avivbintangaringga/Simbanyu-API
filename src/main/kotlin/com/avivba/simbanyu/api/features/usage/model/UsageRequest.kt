package com.avivba.simbanyu.api.features.usage.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class UsageRequest(
    @field:NotBlank
    @field:Length(min = 3, max = 255)
    val subscriberId: String,

    @field:Min(1)
    val currentMeter: Int,

    @field:Length(min = 0, max = 255)
    val operatorNotes: String? = null,
)
