package com.avivba.simbanyu.api.features.report.model

data class ReportDto(
    val id: String,
    val address: String,
    val description: String,
    val status: String,
    val evidenceImageUrl: String,
    val operatorNotes: String? = null,
    val handlingEvidenceImageUrl: String? = null,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun fromEntity(report: Report) = ReportDto(
            id = report.id.toString(),
            address = report.address,
            description = report.description,
            status = report.status.name,
            evidenceImageUrl = report.evidenceImage.url,
            operatorNotes = report.operatorNotes,
            handlingEvidenceImageUrl = report.handlingEvidenceImage?.url,
            createdAt = report.createdAt.toString(),
            updatedAt = report.updatedAt.toString(),
        )
    }
}