package com.avivba.simbanyu.api.features.payment

import com.avivba.simbanyu.api.features.payment.model.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.*

interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findPaymentByOrderId(orderId: String): MutableList<Payment>

    @Modifying
    @Query("UPDATE Payment p SET p.createdAt = :date, p.updatedAt = :date WHERE p.id = :id")
    fun updateTimestamps(id: UUID, date: Instant)
}