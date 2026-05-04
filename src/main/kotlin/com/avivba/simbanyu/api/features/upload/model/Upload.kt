package com.avivba.simbanyu.api.features.upload.model

import jakarta.persistence.*
import java.util.*

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