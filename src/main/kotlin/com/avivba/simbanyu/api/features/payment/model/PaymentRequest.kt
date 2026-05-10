package com.avivba.simbanyu.api.features.payment.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class PaymentRequest(
    @field:NotEmpty
    val billIds: List<String>,

    @field:NotBlank
    val callbackUrl: String,
)
