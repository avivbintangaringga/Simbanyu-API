package com.avivba.simbanyu.api.features.report

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
) {
    companion object {
        fun fromEntity(report: Report) = ReportDto(
            id = report.id.toString(),
            address = report.address,
            description = report.description,
            status = report.status.name,
            evidenceImageURL = report.evidenceImageUrl,
            operatorNotes = report.operatorNotes,
            handlingEvidenceImageURL = report.handlingEvidenceImageUrl,
            createdAt = report.createdAt.toString(),
            updatedAt = report.updatedAt.toString(),
        )
    }
}