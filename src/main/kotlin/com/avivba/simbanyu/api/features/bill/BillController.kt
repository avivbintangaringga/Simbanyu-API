package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.bill.model.BillDto
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.utils.hasRole
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bills")
class BillController(
    private val billService: BillService,
) {
    @GetMapping
    @ResponseMessage("Bills retrieved successfully.")
    fun getAllBills(
        @RequestParam("subscriberId") subscriberId: String? = null,
        @RequestParam("limit") limit: Int? = null,
        @RequestParam("activeOnly") activeOnly: Boolean? = null,
        authentication: Authentication
    ): List<BillDto> {
        return billService.getAll(
            authentication.name,
            subscriberId,
            limit,
            activeOnly
        ).map { BillDto.fromEntity(it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or @billSecurity.isOwnBill(#id, authentication)")
    @ResponseMessage("Bill retrieved successfully.")
    fun getBillById(
        @PathVariable id: String
    ): BillDto {
        return billService.getBillById(id).let {
            BillDto.fromEntity(it)
        }
    }

    // TODO: Download receipt
}