package com.avivba.simbanyu.api.features.report.model

import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import com.avivba.simbanyu.api.features.upload.model.Upload
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "reports")
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: UUID? = null,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = true)
    var longitude: Double? = null,

    @Column(nullable = true)
    var latitude: Double? = null,

    @OneToOne(
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var evidenceImage: Upload,

    var operatorNotes: String? = null,

    @OneToOne(
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var handlingEvidenceImage: Upload? = null,

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
