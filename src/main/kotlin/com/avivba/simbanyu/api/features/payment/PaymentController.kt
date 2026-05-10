package com.avivba.simbanyu.api.features.payment

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.payment.model.CashPaymentRequest
import com.avivba.simbanyu.api.features.payment.model.PaymentCallbackRequest
import com.avivba.simbanyu.api.features.payment.model.PaymentDto
import com.avivba.simbanyu.api.features.payment.model.PaymentRequest
import com.avivba.simbanyu.api.features.payment.model.PaymentResponse
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    @PostMapping("/cash")
    @ResponseMessage("Payment request submitted successfully.")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    fun requestCashPayment(
        @Valid @RequestBody body: CashPaymentRequest,
    ): List<PaymentDto> {
        return paymentService.requestCashPayment(
            body.billIds
        ).map { PaymentDto.fromEntity(it) }
    }

    @PostMapping
    @ResponseMessage("Payment request submitted successfully.")
    fun requestPayment(
        @Valid @RequestBody body: PaymentRequest,
    ): PaymentResponse {
        return paymentService.requestPayment(
            body.billIds,
            body.callbackUrl
        ).let { PaymentResponse(it) }
    }
}