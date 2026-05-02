package com.avivba.simbanyu.api.features.prices.model

data class PriceDto(
    val id: String,
    val name: String,
    val tierOrder: Int,
    val maxVolume: Int?,
    val pricePerMeter: Double
) {
    companion object {
        fun fromEntity(price: Price) = PriceDto(
            id = price.id.toString(),
            name = price.name,
            tierOrder = price.tierOrder,
            maxVolume = price.maxVolume,
            pricePerMeter = price.pricePerMeter
        )
    }
}