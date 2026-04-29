package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.features.subscriber.Subscriber
import com.avivba.simbanyu.api.features.usage.Usage
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.Collections.emptyList
import java.util.UUID

@Entity
@Table(name = "bills")
class Bill(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: UUID? = null,

    @Column(nullable = false)
    var amount: Double = 0.0,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: BillStatus = BillStatus.UNPAID,

    @ManyToOne
    var subscriber: Subscriber,

    @OneToOne
    var usage: Usage,

    @OneToMany(mappedBy = "bill")
    var billItems: MutableList<BillItem> = emptyList(),
)