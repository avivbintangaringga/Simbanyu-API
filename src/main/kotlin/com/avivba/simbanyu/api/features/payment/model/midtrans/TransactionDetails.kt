package com.avivba.simbanyu.api.features.payment.model.midtrans

import com.fasterxml.jackson.annotation.JsonProperty

data class TransactionDetails(
    @JsonProperty("order_id")
    val orderId: String,

    @JsonProperty("gross_amount")
    val grossAmount: Int
)