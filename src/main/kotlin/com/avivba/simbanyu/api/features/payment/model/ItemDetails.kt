package com.avivba.simbanyu.api.features.payment.model

data class ItemDetails(
    val id: String,
    val price: Int,
    val quantity: Int,
    val name: String
)