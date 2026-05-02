package com.avivba.simbanyu.api.features.midtrans.model

import com.fasterxml.jackson.annotation.JsonProperty

data class CustomerDetails(
    @JsonProperty("first_name")
    val firstName: String,

    @JsonProperty("last_name")
    val lastName: String,

    val phone: String,
)