package com.avivba.simbanyu.api.features.report.model

import com.avivba.simbanyu.api.features.subscriber.model.SubscriberDto

data class ReportDto(
    val id: String,
    val subscriber: SubscriberDto,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
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
            subscriber = SubscriberDto.fromEntity(report.subscriber),
            address = report.address,
            latitude = report.latitude,
            longitude = report.longitude,
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