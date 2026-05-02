package com.avivba.simbanyu.api.features.user.model

import com.avivba.simbanyu.api.features.activity.model.Activity
import com.avivba.simbanyu.api.features.fcm.model.FcmToken
import com.avivba.simbanyu.api.features.notification.model.Notification
import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import jakarta.persistence.CascadeType
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
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    var id: UUID? = null,

    @Column(nullable = false, unique = true)
    var username: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false, unique = true)
    var phoneNumber: String = "",

    @Column(nullable = true)
    var photoImageURL: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.SUBSCRIBER,

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var fcmTokens: MutableList<FcmToken> = mutableListOf(),

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var notifications: MutableList<Notification> = mutableListOf(),

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var activities: MutableList<Activity> = mutableListOf(),

    @OneToOne(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var subscriber: Subscriber? = null,

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),
)


