package com.avivba.simbanyu.api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "price")
data class PriceProperties(
    val admin: Double
)