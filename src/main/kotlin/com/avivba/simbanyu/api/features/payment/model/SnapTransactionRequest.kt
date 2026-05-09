package com.avivba.simbanyu.api.features.payment.model

import com.avivba.simbanyu.api.features.payment.model.midtrans.CustomerDetails
import com.avivba.simbanyu.api.features.payment.model.midtrans.TransactionDetails
import com.fasterxml.jackson.annotation.JsonProperty

data class SnapTransactionRequest(
    @JsonProperty("transaction_details")
    val transactionDetails: TransactionDetails,

    val callbacks: Callback,

    @JsonProperty("item_details")
    val itemDetails: List<ItemDetails>,

    @JsonProperty("customer_details")
    val customerDetails: CustomerDetails
)

