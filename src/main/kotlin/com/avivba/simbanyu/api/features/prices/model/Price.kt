package com.avivba.simbanyu.api.features.prices.model

import com.avivba.simbanyu.api.features.plans.model.Plan
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "prices")
class Price(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: UUID? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var tierOrder: Int = 0,

    @ManyToOne(
        fetch = FetchType.LAZY
    )
    var plan: Plan,

    @Column(nullable = true)
    var maxVolume: Int? = null,

    @Column(nullable = false)
    var pricePerMeter: Double = 0.0,
)