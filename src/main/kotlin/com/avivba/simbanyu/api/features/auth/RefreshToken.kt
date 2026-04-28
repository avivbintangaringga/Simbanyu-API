package com.avivba.simbanyu.api.features.auth

import com.avivba.simbanyu.api.features.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "refresh_token")
class RefreshToken(
    @Id
    @Column(nullable = false, unique = true)
    var token: String,

    @ManyToOne
    var user: User
)