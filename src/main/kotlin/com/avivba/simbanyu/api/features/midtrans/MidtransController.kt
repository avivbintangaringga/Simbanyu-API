package com.avivba.simbanyu.api.features.midtrans

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.bill.BillRepository
import com.avivba.simbanyu.api.features.bill.model.BillStatus
import com.avivba.simbanyu.api.features.midtrans.model.PaymentCallbackRequest
import com.avivba.simbanyu.api.features.midtrans.model.RequestPaymentRequest
import com.avivba.simbanyu.api.features.midtrans.model.RequestPaymentResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/payments")
class MidtransController(
    private val midtransService: MidtransService,
    private val billRepository: BillRepository,
) {
    @PostMapping("/callback")
    fun paymentCallback(
        @RequestBody body: PaymentCallbackRequest
    ): ApiResponse<Unit> {
        midtransService.handleCallback(body)
        return ApiResponse.success(
            "Payment callback received successfully.",
            Unit
        )
    }

    @PostMapping
    fun requestPayment(
        @Valid @RequestBody body: RequestPaymentRequest,
    ): ApiResponse<RequestPaymentResponse> {
        val billIds = body.billIds.map { UUID.fromString(it) }.toMutableList()
        val bills = billRepository.findBillsByIdIsIn(
            ids = billIds
        )

        if (bills.isEmpty()) {
            throw IllegalArgumentException("Bills not found")
        }

        if (bills.any { it.status != BillStatus.UNPAID }) {
            throw IllegalArgumentException("Bills must be unpaid to request payment.")
        }

        val paymentUrl = midtransService.requestSnapPayment(
            bills = bills,
            callbackUrl = body.callbackUrl
        )

        return ApiResponse.success(
            "Payment request initiated successfully.",
            RequestPaymentResponse(paymentUrl)
        )
    }
}