package com.avivba.simbanyu.api.features.report.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

data class HandlingRequest(
    val status: ReportStatus,

    val operatorNotes: String? = null,
)
