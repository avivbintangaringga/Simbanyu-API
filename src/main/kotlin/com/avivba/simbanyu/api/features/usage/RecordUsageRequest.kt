package com.avivba.simbanyu.api.features.usage

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class RecordUsageRequest(
    @NotBlank
    val subscriberId: String,

    @NotBlank
    @Min(1)
    val currentMeter: Int,

    @NotBlank
    @URL
    val evidenceImageURL: String,

    @NotBlank
    val operatorNotes: String? = null,
)
