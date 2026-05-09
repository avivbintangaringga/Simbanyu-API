package com.avivba.simbanyu.api.features.payment.model

import com.avivba.simbanyu.api.features.bill.model.Bill
import com.avivba.simbanyu.api.features.bill.model.BillStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "payments")
class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,

    @Column(nullable = true)
    var orderId: String,

    @Column(nullable = true)
    var paymentType: String? = null,

    @Column(nullable = true)
    var paymentUrl: String? = null,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus = PaymentStatus.PENDING,

    @ManyToOne
    var bill: Bill,

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),
)