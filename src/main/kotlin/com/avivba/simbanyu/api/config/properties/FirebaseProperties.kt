package com.avivba.simbanyu.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("firebase")
data class FirebaseProperties(
    val serviceAccount: String
)