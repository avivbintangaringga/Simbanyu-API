package com.avivba.simbanyu.api.dto

data class ReportDto(
    val id: String,
    val address: String,
    val description: String,
    val status: String,
    val evidenceImageURL: String,
    val operatorNotes: String? = null,
    val handlingEvidenceImageURL: String? = null,
    val createdAt: String,
    val updatedAt: String,
)
