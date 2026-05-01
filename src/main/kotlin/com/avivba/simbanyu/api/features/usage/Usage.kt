package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.features.bill.Bill
import com.avivba.simbanyu.api.features.subscriber.Subscriber
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "usages")
class Usage(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: UUID? = null,

    @Column(nullable = false)
    var meterStart: Int,

    @Column(nullable = false)
    var meterEnd: Int,

    @Column(nullable = false)
    var isInitial: Boolean = false,

    @Column(nullable = false)
    var evidenceImageUrl: String,

    @Column(nullable = true)
    var operatorNotes: String? = null,

    @ManyToOne
    var subscriber: Subscriber,

    @OneToOne(
        mappedBy = "usage",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var bill: Bill? = null,

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),
)