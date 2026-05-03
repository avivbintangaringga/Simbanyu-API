package com.avivba.simbanyu.api.features.upload

import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/uploads")
class UploadController(
    private val uploadService: UploadService
) {
    @GetMapping("/{id}")
    fun getFile(
        @PathVariable id: String
    ): ResponseEntity<Resource> {
        return  uploadService.getFile(id).let {
            ResponseEntity.ok().body(it)
        }
    }
}