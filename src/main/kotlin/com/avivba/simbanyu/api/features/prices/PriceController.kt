package com.avivba.simbanyu.api.features.prices

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.prices.model.PriceDto
import com.avivba.simbanyu.api.features.prices.model.PriceRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/plans")
class PriceController(
    private val priceService: PriceService
) {
    @GetMapping("/{planId}/prices")
    fun getPlans(
        @PathVariable planId: String
    ): ApiResponse<List<PriceDto>> {
        return priceService.getAllPricesOfAPlan(planId)
            .map { PriceDto.fromEntity(it) }
            .let { ApiResponse.success(it) }
    }

    @PostMapping("/{planId}/prices")
    fun addPlan(
        @PathVariable planId: String,
        @Valid @RequestBody body: PriceRequest
    ): ApiResponse<PriceDto> {
        return priceService.addPrice(planId, body)
            .let { ApiResponse.success(PriceDto.fromEntity(it)) }
    }

    @DeleteMapping("/{planId}/prices/{id}")
    fun deletePlan(
        @PathVariable id: String,
        @PathVariable planId: String
    ): ApiResponse<Unit> {
        return priceService.deletePrice(id)
            .let { ApiResponse.success(Unit) }
    }
}