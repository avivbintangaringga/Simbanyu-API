package com.avivba.simbanyu.api.features.midtrans

import com.avivba.simbanyu.api.features.bill.model.Bill
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "midtrans_payments")
class MidtransPayment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,

    @Column(nullable = false)
    var orderId: String,

    @ManyToOne
    var bill: Bill,
)