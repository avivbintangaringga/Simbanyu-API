package com.avivba.simbanyu.api.features.prices

import com.avivba.simbanyu.api.features.plans.PlanRepository
import com.avivba.simbanyu.api.features.prices.model.Price
import com.avivba.simbanyu.api.features.prices.model.PriceRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PriceService(
    private val priceRepository: PriceRepository,
    private val planRepository: PlanRepository,
) {
    fun getAllPricesOfAPlan(planId: String): List<Price> {
        return priceRepository.findByPlan_IdOrderByTierOrder(UUID.fromString(planId))
    }

    fun getPriceById(id: String): Price {
        return priceRepository.findById(UUID.fromString(id)).orElseThrow()
    }

    fun addPrice(
        planId: String,
        data: PriceRequest
    ): Price {
        val plan = planRepository.findById(UUID.fromString(planId)).orElseThrow()

        val price = Price(
            name = data.name,
            tierOrder = data.tierOrder,
            plan = plan,
            maxVolume = data.maxVolume,
            pricePerMeter = data.pricePerMeter,
        )

        return priceRepository.save(price)
    }

    fun updatePrice(id: String, data: PriceRequest): Price {
        val price = priceRepository.findById(UUID.fromString(id)).orElseThrow()
        price.name = data.name
        price.tierOrder = data.tierOrder
        price.maxVolume = data.maxVolume
        price.pricePerMeter = data.pricePerMeter
        return priceRepository.save(price)
    }

    fun deletePrice(id: String) {
        val price = priceRepository.findById(UUID.fromString(id)).orElseThrow()
        priceRepository.delete(price)
    }
}