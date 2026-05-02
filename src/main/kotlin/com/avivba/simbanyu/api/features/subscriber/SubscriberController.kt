package com.avivba.simbanyu.api.features.subscriber

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.subscriber.model.AddSubscriberRequest
import com.avivba.simbanyu.api.features.subscriber.model.SubscriberDto
import com.avivba.simbanyu.api.features.subscriber.model.UpdateSubscriberRequest
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/subscribers")
class SubscriberController(
    val subscriberService: SubscriberService
) {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    fun getAllSubscribers(): ApiResponse<List<SubscriberDto>> {
        return subscriberService.getAllSubscribers().let {
            ApiResponse.success(it)
        }
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or #returnObject.data.user.username == principal.username")
    fun getSubscriberById(
        @PathVariable id: String
    ): ApiResponse<SubscriberDto> {
        return subscriberService.getSubscriberById(id).let {
            ApiResponse.success(it)
        }
    }

    @PostMapping
    fun addSubscriber(
        @Valid @RequestBody body: AddSubscriberRequest
    ): ApiResponse<SubscriberDto> {
        return subscriberService.addSubscriber(body).let {
            ApiResponse.success(SubscriberDto.fromEntity(it))
        }
    }

    @PutMapping("/{id}")
    fun updateSubscriber(
        @PathVariable id: String,
        @Valid @RequestBody body: UpdateSubscriberRequest
    ): ApiResponse<SubscriberDto> {
        return subscriberService.updateSubscriber(id, body).let {
            ApiResponse.success(SubscriberDto.fromEntity(it))
        }
    }

    @DeleteMapping("/{id}")
    fun deleteSubscriber(
        @PathVariable id: String
    ) = subscriberService.deleteSubscriberById(id).let {
        ApiResponse.success(it)
    }
}