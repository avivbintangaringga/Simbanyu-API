package com.avivba.simbanyu.api.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "usages")
class Usage(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: String? = null,

    @Column(nullable = false)
    var meterStart: Int,

    @Column(nullable = false)
    var amount: Int,

    @Column(nullable = false)
    var evidenceImageUrl: String,

    @Column(nullable = false)
    var operatorNotes: String,

    @ManyToOne
    var subscriber: Subscriber,

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),
)