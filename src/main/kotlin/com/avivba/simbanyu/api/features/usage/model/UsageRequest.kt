package com.avivba.simbanyu.api.features.usage.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class UsageRequest(
    @NotBlank
    val subscriberId: String,

    @Min(1)
    val currentMeter: Int,

    val operatorNotes: String? = null,
)
