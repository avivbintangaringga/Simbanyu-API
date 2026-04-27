package com.avivba.simbanyu.api.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.Collections.emptyList

@Entity
@Table(name = "bills")
class Bill(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: String? = null,

    @Column(nullable = false)
    var amount: Double = 0.0,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: BillStatus = BillStatus.UNPAID,

    @ManyToOne
    var subscriber: Subscriber,

    @OneToMany(mappedBy = "bill")
    var billItems: MutableList<BillItem> = emptyList(),
)

enum class BillStatus {
    PAID,
    UNPAID,
    PENDING
}
