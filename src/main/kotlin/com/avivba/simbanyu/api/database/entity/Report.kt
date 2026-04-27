package com.avivba.simbanyu.api.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "reports")
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: String? = null,

    @Column(nullable = false)
    var address: String = "",

    @Column(nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var evidenceImageUrl: String = "",

    var operatorNotes: String? = null,
    var handlingEvidenceImageUrl: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ReportStatus = ReportStatus.WAITING,

    @ManyToOne
    var subscriber: Subscriber,

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),
)

enum class ReportStatus {
    WAITING,
    IN_PROGRESS,
    COMPLETED,
    REJECTED
}