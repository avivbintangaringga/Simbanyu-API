package com.avivba.simbanyu.api.features.subscriber

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.subscriber.model.AddSubscriberRequest
import com.avivba.simbanyu.api.features.subscriber.model.SubscriberDto
import com.avivba.simbanyu.api.features.subscriber.model.UpdateSubscriberRequest
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/subscribers")
class SubscriberController(
    val subscriberService: SubscriberService
) {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @ResponseMessage("Subscribers retrieved successfully.")
    fun getAllSubscribers(): List<SubscriberDto> {
        return subscriberService.getAllSubscribers()
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or #returnObject.data.user.username == principal.username")
    @ResponseMessage("Subscriber retrieved successfully.")
    fun getSubscriberById(
        @PathVariable id: String
    ): SubscriberDto {
        return subscriberService.getSubscriberById(id)
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Subscriber created successfully.")
    fun addSubscriber(
        @Valid @RequestBody body: AddSubscriberRequest,
        authentication: Authentication
    ): SubscriberDto {
        return subscriberService.addSubscriber(
            data = body,
            username = authentication.name
        ).let {
            SubscriberDto.fromEntity(it)
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Subscriber updated successfully.")
    fun updateSubscriber(
        @PathVariable id: String,
        @Valid @RequestBody body: UpdateSubscriberRequest,
        authentication: Authentication
    ): SubscriberDto {
        return subscriberService.updateSubscriber(
            id = id,
            data = body,
            username = authentication.name
        ).let {
            SubscriberDto.fromEntity(it)
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Subscriber deleted successfully.")
    fun deleteSubscriber(
        @PathVariable id: String,
        authentication: Authentication
    ) = subscriberService.deleteSubscriberById(
        id = id,
        username = authentication.name
    )
}