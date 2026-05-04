package com.avivba.simbanyu.api.features.report.model

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class HandlingRequest(
    @field:NotBlank
    @field:Length(min = 1, max = 255)
    val status: ReportStatus,

    val operatorNotes: String? = null,
)
