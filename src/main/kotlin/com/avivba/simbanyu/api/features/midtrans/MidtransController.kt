package com.avivba.simbanyu.api.features.midtrans

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.bill.BillRepository
import com.avivba.simbanyu.api.features.midtrans.model.PaymentCallbackRequest
import com.avivba.simbanyu.api.features.midtrans.model.RequestPaymentRequest
import com.avivba.simbanyu.api.features.midtrans.model.RequestPaymentResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    ) {
        // TODO: Handle callback from Midtrans
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

        val paymentUrl = midtransService.requestSnapPayment(
            bills = bills,
            callbackUrl = body.callbackUrl
        )

        return ApiResponse.success(
            RequestPaymentResponse(paymentUrl)
        )
    }
}