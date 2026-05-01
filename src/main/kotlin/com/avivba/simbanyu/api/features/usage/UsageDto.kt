package com.avivba.simbanyu.api.features.usage

data class UsageDto(
    val id: String,
    val meterStart: Int,
    val meterEnd: Int,
    val amount: Int,
    val evidenceImageURL: String,
    val operatorNotes: String? = null,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun fromEntity(usage: Usage): UsageDto {
            return UsageDto(
                id = usage.id.toString(),
                meterStart = usage.meterStart,
                meterEnd = usage.meterEnd,
                amount = usage.meterEnd - usage.meterStart,
                evidenceImageURL = usage.evidenceImageUrl,
                operatorNotes = usage.operatorNotes,
                createdAt = usage.createdAt.toString(),
                updatedAt = usage.updatedAt.toString(),
            )
        }
    }
}