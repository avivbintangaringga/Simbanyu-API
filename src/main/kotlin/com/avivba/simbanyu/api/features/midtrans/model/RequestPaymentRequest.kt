package com.avivba.simbanyu.api.features.midtrans.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class RequestPaymentRequest(
    @field:NotEmpty
    val billIds: List<String>,

    // TODO: Set @field to all dto
    @field:NotBlank
    val callbackUrl: String,
)
