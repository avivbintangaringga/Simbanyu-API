package com.avivba.simbanyu.api.features.payment.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SnapTransactionResponse(
    val token: String,

    @JsonProperty("redirect_url")
    val redirectUrl: String
)
