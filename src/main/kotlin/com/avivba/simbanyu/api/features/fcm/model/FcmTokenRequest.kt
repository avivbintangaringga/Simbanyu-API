package com.avivba.simbanyu.api.features.fcm.model

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class FcmTokenRequest(
    @field:NotBlank
    @field:Length(
        min = 10,
        max = 4096
    )
    val token: String
)
