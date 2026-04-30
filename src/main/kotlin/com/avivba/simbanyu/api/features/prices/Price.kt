package com.avivba.simbanyu.api.features.prices

import com.avivba.simbanyu.api.features.plans.Plan
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

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