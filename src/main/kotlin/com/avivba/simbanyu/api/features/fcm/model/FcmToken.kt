package com.avivba.simbanyu.api.features.fcm.model

import com.avivba.simbanyu.api.features.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.validator.constraints.Length
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "fcm_tokens")
class FcmToken(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: UUID? = null,

    @Column(nullable = false)
    @Length(
        min = 10,
        max = 4096
    )
    var token: String,

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    var user: User,

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),
)