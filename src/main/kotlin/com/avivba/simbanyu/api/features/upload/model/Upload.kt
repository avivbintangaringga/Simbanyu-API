package com.avivba.simbanyu.api.features.upload.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "uploads")
class Upload(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", nullable = false, unique = true)
    var id: UUID? = null,

    @Column(nullable = false)
    var contentType: String,
) {
    val url: String
            get() = id?.let { "/uploads/$id" } ?: ""
}