package com.avivba.simbanyu.api.features.auth.model

import com.avivba.simbanyu.api.features.user.model.User
import jakarta.persistence.*

@Entity
@Table(name = "refresh_token")
class RefreshToken(
    @Id
    @Column(nullable = false, unique = true)
    var token: String,

    @ManyToOne
    var user: User
)