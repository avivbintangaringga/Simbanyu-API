package com.avivba.simbanyu.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("midtrans")
data class MidtransProperties(
    val serverKey: String,
    val apiUrl: String,
    val snapUrl: String
)