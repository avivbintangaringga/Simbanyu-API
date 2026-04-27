package com.avivba.simbanyu.api.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.Collections.emptyList
import java.util.UUID

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
    @Enumerated(EnumType.STRING)
    var subscriberType: SubscriberType = SubscriberType.RESIDENTIAL,

    @Column(nullable = false)
    var address: String = "",

    @Column(nullable = false)
    var longitude: Double = 0.0,

    @Column(nullable = false)
    var latitude: Double = 0.0,

    @OneToMany(mappedBy = "subscriber")
    var usages: MutableList<Usage> = emptyList(),

    @OneToMany(mappedBy = "subscriber")
    var reports: MutableList<Report> = emptyList(),

    @OneToMany(mappedBy = "subscriber")
    var bills: MutableList<Bill> = emptyList(),

    @OneToOne
    var user: User,

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),
)

enum class SubscriberType {
    SOCIAL,
    RESIDENTIAL,
    COMMERCIAL
}