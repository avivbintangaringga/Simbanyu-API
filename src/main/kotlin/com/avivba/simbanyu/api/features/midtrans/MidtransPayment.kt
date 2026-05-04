package com.avivba.simbanyu.api.features.midtrans

import com.avivba.simbanyu.api.features.bill.model.Bill
import jakarta.persistence.*
import java.util.*

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