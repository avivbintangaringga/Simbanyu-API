package com.avivba.simbanyu.api.dto

data class BillDto(
    val id: String,
    val title: String,
    val amount: Double,
    val status: String,
    val items: List<BillItemDto>,
    val createdAt: String,
    val updatedAt: String,
)
