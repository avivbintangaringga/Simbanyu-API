package com.avivba.simbanyu.api.features.payment.model

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentCallbackRequest(
    @JsonProperty("transaction_time")
    val transactionTime: String,

    @JsonProperty("transaction_status")
    val transactionStatus: String,

    @JsonProperty("transaction_id")
    val transactionId: String,

    @JsonProperty("status_message")
    val statusMessage: String,

    @JsonProperty("status_code")
    val statusCode: String,

    @JsonProperty("signature_key")
    val signatureKey: String,

    @JsonProperty("payment_type")
    val paymentType: String,

    @JsonProperty("order_id")
    val orderId: String,

    @JsonProperty("merchant_id")
    val merchantId: String,

    @JsonProperty("gross_amount")
    val grossAmount: String
)