package com.avivba.simbanyu.api.features.usage.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class UsageUpdateRequest(
    @field:Min(1)
    val currentMeter: Int,

    @field:Length(min = 0, max = 255)
    val operatorNotes: String? = null,
)
