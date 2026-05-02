package com.avivba.simbanyu.api.features.prices

import com.avivba.simbanyu.api.features.prices.model.Price
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PriceRepository : JpaRepository<Price, UUID> {
    fun findAllByPlan_Id(planId: UUID): MutableList<Price>
    fun findByPlan_IdOrderByTierOrder(planId: UUID): MutableList<Price>
}