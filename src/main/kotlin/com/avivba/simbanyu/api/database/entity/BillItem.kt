package com.avivba.simbanyu.api.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table


@Entity
@Table(name = "bill_items")
class BillItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: String? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var amount: Double = 0.0,

    @ManyToOne
    var bill: Bill? = null,
)