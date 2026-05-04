package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.config.ResponseMessage
import com.avivba.simbanyu.api.features.bill.model.BillDto
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.utils.hasRole
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bills")
class BillController(
    private val billService: BillService,
    private val subscriberRepository: SubscriberRepository,
) {
    @GetMapping
    @ResponseMessage("Bills retrieved successfully.")
    fun getAllBills(
        authentication: Authentication
    ): List<BillDto> {
        val isAdmin = authentication.hasRole("ADMIN")
        val isOperator = authentication.hasRole("OPERATOR")

        return (if (isAdmin || isOperator) {
            billService.getAllBills()
        } else {
            val subscriber = subscriberRepository.findSubscriberByUser_Username(authentication.name)
            billService.getBillsBySubscriberId(subscriber[0].id.toString())
        }).map { BillDto.fromEntity(it) }
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
}