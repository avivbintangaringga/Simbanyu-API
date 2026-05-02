package com.avivba.simbanyu.api.features.files

import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
@RequestMapping("/files")
class FilesController(
    private val filesService: FilesService
) {
    @GetMapping("/{id}")
    fun getFile(
        @PathVariable id: String
    ): ResponseEntity<Resource> {
        return  filesService.getFile(id).let {
            ResponseEntity.ok().body(it)
        }
    }
}