package com.avivba.simbanyu.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "price")
data class PriceProperties(
    val admin: Double
)