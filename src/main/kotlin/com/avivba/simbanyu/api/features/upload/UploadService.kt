package com.avivba.simbanyu.api.features.upload

import com.avivba.simbanyu.api.config.properties.FileStorageProperties
import com.avivba.simbanyu.api.features.upload.model.Upload
import com.avivba.simbanyu.api.features.upload.model.UploadResource
import com.avivba.simbanyu.api.utils.detectContentType
import jakarta.persistence.EntityNotFoundException
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Service
class UploadService(
    private val storageProps: FileStorageProperties,
    private val uploadRepository: UploadRepository
) {
    private val maxBase64Size = ((storageProps.maxFileSize + 2) / 3) * 4

    fun getFile(id: String): UploadResource {
        if (!validateFileId(id)) {
            throw IllegalArgumentException("Invalid file ID")
        }

        val upload = uploadRepository.findById(UUID.fromString(id)).orElseThrow()

        val file = File(storageProps.uploadDir, id)
        if (!file.exists()) {
            throw EntityNotFoundException("File not found")
        }
        val resource = FileSystemResource(file)

        return UploadResource(
            upload = upload,
            resource = resource
        )
    }

    fun saveMultipart(file: MultipartFile): Upload {
        if (file.isEmpty) {
            throw IllegalArgumentException("File is empty")
        }

        if (file.size > storageProps.maxFileSize) {
            throw IllegalArgumentException("File size exceeds the limit")
        }

        file.inputStream.use { input ->
            val header = input.readNBytes(512)
            val contentType = detectContentType(header)
            if (!storageProps
                    .allowedFileTypes
                    .contains(contentType)
            ) {
                throw IllegalArgumentException("File type not allowed")
            }

            val upload = uploadRepository.save(
                Upload(
                    contentType = contentType,
                )
            )
            val targetFile = File(storageProps.uploadDir, upload.id.toString())
            targetFile.parentFile.mkdirs()
            targetFile.outputStream().use { output ->
                output.write(header)
                input.copyTo(output)
            }

            return upload
        }
    }

    fun deleteFile(file: Upload) {
        val f = File(storageProps.uploadDir, file.id.toString())
        f.delete()
        uploadRepository.delete(file)
    }

    fun saveBase64(base64: String): String {
        val cleanedBase64 = base64.substringAfter("base64,")
        if (cleanedBase64.length > maxBase64Size) {
            throw IllegalArgumentException("File size exceeds the limit")
        }

        val id = UUID.randomUUID().toString()
        val content = Base64.getDecoder().decode(cleanedBase64)

        if (content.size > storageProps.maxFileSize) {
            throw IllegalArgumentException("File size exceeds the limit")
        }

        if (!storageProps.allowedFileTypes.contains(detectContentType(content))) {
            throw IllegalArgumentException("File type not allowed")
        }

        val file = File(storageProps.uploadDir, id)
        file.writeBytes(content)
        return id
    }

    private fun validateFileId(id: String): Boolean {
        if (id.isBlank()) {
            throw IllegalArgumentException("File ID cannot be empty")
        }

        return try {
            UUID.fromString(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}