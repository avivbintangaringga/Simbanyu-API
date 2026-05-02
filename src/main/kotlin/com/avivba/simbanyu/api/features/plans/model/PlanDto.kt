package com.avivba.simbanyu.api.features.plans.model

import com.avivba.simbanyu.api.features.prices.model.PriceDto

data class PlanDto(
    val id: String,
    val name: String,
    val basePrice: Double,
    val prices: List<PriceDto>
) {
    companion object {
        fun fromEntity(plan: Plan) = PlanDto(
            id = plan.id.toString(),
            name = plan.name,
            basePrice = plan.basePrice,
            prices = plan.prices.map {
                PriceDto.fromEntity(it)
            },
        )
    }
}
