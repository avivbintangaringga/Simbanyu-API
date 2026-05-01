package com.avivba.simbanyu.api.features.usage

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class UsageRequest(
    @NotBlank
    val subscriberId: String,

    @Min(1)
    val currentMeter: Int,

    @NotBlank
    val evidenceImage: String,

    val operatorNotes: String? = null,
)
