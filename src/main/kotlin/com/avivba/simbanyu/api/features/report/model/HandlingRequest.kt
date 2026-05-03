package com.avivba.simbanyu.api.features.report.model

import jakarta.validation.constraints.NotBlank

data class HandlingRequest(
    @NotBlank
    val status: ReportStatus,

    val operatorNotes: String? = null,
)
