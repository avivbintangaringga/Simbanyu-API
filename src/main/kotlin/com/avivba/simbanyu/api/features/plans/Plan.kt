package com.avivba.simbanyu.api.features.plans

import com.avivba.simbanyu.api.features.prices.Price
import com.avivba.simbanyu.api.features.subscriber.Subscriber
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
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    var prices: MutableList<Price> = mutableListOf(),

    @OneToMany(
        mappedBy = "plan",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var subscribers: MutableList<Subscriber> = mutableListOf()
)