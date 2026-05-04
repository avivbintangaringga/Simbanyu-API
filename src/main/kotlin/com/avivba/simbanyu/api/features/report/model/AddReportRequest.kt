package com.avivba.simbanyu.api.features.report.model

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class AddReportRequest(
    @field:NotBlank
    @field:Length(min = 3, max = 255)
    val address: String,

    @field:NotBlank
    @field:Length(min = 3, max = 1024)
    val description: String,

    val latitude: Double? = null,
    val longitude: Double? = null,
)
