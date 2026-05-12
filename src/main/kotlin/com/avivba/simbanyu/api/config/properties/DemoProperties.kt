package com.avivba.simbanyu.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo")
data class DemoProperties(
    val enabled: Boolean
)
