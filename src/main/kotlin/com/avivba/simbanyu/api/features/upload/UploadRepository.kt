package com.avivba.simbanyu.api.features.upload

import com.avivba.simbanyu.api.features.upload.model.Upload
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UploadRepository: JpaRepository<Upload, UUID>