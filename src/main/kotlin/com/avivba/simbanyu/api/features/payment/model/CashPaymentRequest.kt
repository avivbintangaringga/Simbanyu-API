package com.avivba.simbanyu.api.features.payment.model

import jakarta.validation.constraints.NotEmpty

data class CashPaymentRequest(
    @field:NotEmpty
    val billIds: List<String>
)
