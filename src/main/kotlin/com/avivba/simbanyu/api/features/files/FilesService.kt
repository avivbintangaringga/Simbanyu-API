package com.avivba.simbanyu.api.features.files

import com.avivba.simbanyu.api.config.FileStorageProperties
import com.avivba.simbanyu.api.utils.detectContentType
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.Base64
import java.util.UUID

@Service
class FilesService(
    private val storageProps: FileStorageProperties
) {
    private val maxBase64Size = ((storageProps.maxFileSize + 2) / 3) * 4

    fun getFile(id: String): FileSystemResource {
        if (!validateFileId(id)) {
            throw RuntimeException("Invalid file ID")
        }

        val file = File(storageProps.uploadDir, id)

        if (!file.exists()) {
            throw RuntimeException("File not found")
        }

        return FileSystemResource(file)
    }

    fun getFileUrl(id: String): String {
        return "/files/${id}"
    }

    fun saveMultipart(file: MultipartFile): String {
        if (file.isEmpty) {
            throw RuntimeException("File is empty")
        }

        if (file.size > storageProps.maxFileSize) {
            throw RuntimeException("File size exceeds the limit")
        }

        val id = UUID.randomUUID().toString()
        val targetFile = File(storageProps.uploadDir, id)

        targetFile.parentFile.mkdirs()

        file.inputStream.use { input ->
            val header = input.readNBytes(512)

            if (!storageProps
                    .allowedFileTypes
                    .contains(detectContentType(header))
            ) {
                throw RuntimeException("File type not allowed")
            }

            targetFile.outputStream().use { output ->
                output.write(header)
                input.copyTo(output)
            }
        }

        return id
    }

    fun saveBase64(base64: String): String {
        val cleanedBase64 = base64.substringAfter("base64,")
        if (cleanedBase64.length > maxBase64Size) {
            throw RuntimeException("File size exceeds the limit")
        }

        val id = UUID.randomUUID().toString()
        val content = Base64.getDecoder().decode(cleanedBase64)

        if (content.size > storageProps.maxFileSize) {
            throw RuntimeException("File size exceeds the limit")
        }

        if (!storageProps.allowedFileTypes.contains(detectContentType(content))) {
            throw RuntimeException("File type not allowed")
        }

        val file = File(storageProps.uploadDir, id)
        file.writeBytes(content)
        return id
    }

    private fun validateFileId(id: String): Boolean {
        if (id.isBlank()) {
            throw RuntimeException("File ID cannot be empty")
        }

        return try {
            UUID.fromString(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}