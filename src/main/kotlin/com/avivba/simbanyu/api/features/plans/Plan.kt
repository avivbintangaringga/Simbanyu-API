package com.avivba.simbanyu.api.features.plans

import com.avivba.simbanyu.api.features.prices.Price
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "plans")
class Plan(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    var id: UUID? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var basePrice: Double = 0.0,

    @OneToMany(
        mappedBy = "plan",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @OrderBy("tierOrder ASC")
    var prices: MutableList<Price> = mutableListOf(),
)