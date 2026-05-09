package com.avivba.simbanyu.api.features.payment

import com.avivba.simbanyu.api.features.payment.model.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findPaymentByOrderId(orderId: String): MutableList<Payment>
}