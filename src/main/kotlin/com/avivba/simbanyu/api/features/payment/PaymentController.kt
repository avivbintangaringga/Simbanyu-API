package com.avivba.simbanyu.api.features.payment

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.bill.BillRepository
import com.avivba.simbanyu.api.features.bill.model.BillStatus
import com.avivba.simbanyu.api.features.payment.model.PaymentCallbackRequest
import com.avivba.simbanyu.api.features.payment.model.RequestPaymentRequest
import com.avivba.simbanyu.api.features.payment.model.RequestPaymentResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService,
) {
    @PostMapping("/callback")
    fun paymentCallback(
        @RequestBody body: PaymentCallbackRequest
    ) {
        paymentService.handleCallback(body)
    }

    // TODO: Pay by cash

    @PostMapping
    @ResponseMessage("Payment request submitted successfully.")
    fun requestPayment(
        @Valid @RequestBody body: RequestPaymentRequest,
    ): RequestPaymentResponse {
        return paymentService.requestPayment(
            body.billIds,
            body.callbackUrl
        ).let { RequestPaymentResponse(it) }
    }
}