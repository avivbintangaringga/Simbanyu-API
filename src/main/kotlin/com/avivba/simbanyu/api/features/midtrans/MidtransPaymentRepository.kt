package com.avivba.simbanyu.api.features.midtrans

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MidtransPaymentRepository : JpaRepository<MidtransPayment, UUID> {
    fun findMidtransPaymentByOrderId(orderId: String): MutableList<MidtransPayment>
}