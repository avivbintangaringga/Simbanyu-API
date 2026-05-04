package com.avivba.simbanyu.api.features.subscriber.model

import com.avivba.simbanyu.api.features.bill.model.Bill
import com.avivba.simbanyu.api.features.plans.model.Plan
import com.avivba.simbanyu.api.features.report.model.Report
import com.avivba.simbanyu.api.features.usage.model.Usage
import com.avivba.simbanyu.api.features.user.model.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import java.util.Collections.emptyList

@Entity
@Table(name = "subscribers")
class Subscriber(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: UUID? = null,

    @Column(nullable = false, unique = true)
    var meterId: String = "",

    @Column(nullable = false)
    var address: String = "",

    @Column(nullable = false)
    var longitude: Double = 0.0,

    @Column(nullable = false)
    var latitude: Double = 0.0,

    @OneToMany(
        mappedBy = "subscriber",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var usages: MutableList<Usage> = emptyList(),

    @OneToMany(
        mappedBy = "subscriber",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var reports: MutableList<Report> = emptyList(),

    @OneToMany(
        mappedBy = "subscriber",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var bills: MutableList<Bill> = emptyList(),

    @ManyToOne
    var plan: Plan,

    @OneToOne
    var user: User,

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),
)
