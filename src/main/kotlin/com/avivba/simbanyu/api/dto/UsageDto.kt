package com.avivba.simbanyu.api.dto

data class UsageDto(
    val id: String,
    val meterStart: Int,
    val amount: Int,
    val evidenceImageURL: String,
    val operatorNotes: String? = null,
    val createdAt: String,
    val updatedAt: String,
)
