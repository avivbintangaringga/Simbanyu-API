package com.avivba.simbanyu.api.features.bill.model

import com.avivba.simbanyu.api.features.payment.model.Payment
import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import com.avivba.simbanyu.api.features.usage.model.Usage
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import java.util.Collections.emptyList

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

    @OneToMany(
        mappedBy = "bill",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var billItems: MutableList<BillItem> = emptyList(),

    @OneToMany(
        mappedBy = "bill",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var payments: MutableList<Payment> = emptyList(),

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),
)