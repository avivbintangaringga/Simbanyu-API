package com.avivba.simbanyu.api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "file-storage")
data class FileStorageProperties(
    val uploadDir: String,
    val maxFileSize: Long,
    val allowedFileTypes: List<String>
)
