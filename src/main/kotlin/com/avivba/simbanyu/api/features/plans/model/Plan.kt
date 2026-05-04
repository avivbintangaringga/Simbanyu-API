package com.avivba.simbanyu.api.features.plans.model

import com.avivba.simbanyu.api.features.prices.model.Price
import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*

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