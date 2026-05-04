package com.avivba.simbanyu.api.features.prices

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.prices.model.PriceDto
import com.avivba.simbanyu.api.features.prices.model.PriceRequest
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
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
    @ResponseMessage("Prices retrieved successfully.")
    fun getPlans(
        @PathVariable planId: String
    ): List<PriceDto> {
        return priceService.getAllPricesOfAPlan(planId)
            .map { PriceDto.fromEntity(it) }
    }

    @PostMapping("/{planId}/prices")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Price created successfully.")
    fun addPlan(
        @PathVariable planId: String,
        @Valid @RequestBody body: PriceRequest
    ): PriceDto {
        return priceService.addPrice(planId, body)
            .let {
                PriceDto.fromEntity(it)
            }
    }

    @DeleteMapping("/{planId}/prices/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Price deleted successfully.")
    fun deletePlan(
        @PathVariable id: String,
        @PathVariable planId: String
    ) {
        priceService.deletePrice(id)
    }
}