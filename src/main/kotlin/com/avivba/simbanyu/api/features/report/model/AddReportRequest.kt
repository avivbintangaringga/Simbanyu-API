package com.avivba.simbanyu.api.features.report.model

import jakarta.validation.constraints.NotBlank

data class AddReportRequest(
    @NotBlank
    val address: String,

    @NotBlank
    val description: String,

    @NotBlank
    val evidenceImage: String,

    val latitude: Double? = null,
    val longitude: Double? = null,
)
