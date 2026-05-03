package com.avivba.simbanyu.api.features.upload.model

import org.springframework.core.io.Resource

data class UploadResource(
    val upload: Upload,
    val resource: Resource
)
