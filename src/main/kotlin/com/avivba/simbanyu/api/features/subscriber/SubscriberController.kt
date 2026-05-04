package com.avivba.simbanyu.api.features.subscriber

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
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
        @Valid @RequestBody body: AddSubscriberRequest
    ): SubscriberDto {
        return subscriberService.addSubscriber(body).let {
            SubscriberDto.fromEntity(it)
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Subscriber updated successfully.")
    fun updateSubscriber(
        @PathVariable id: String,
        @Valid @RequestBody body: UpdateSubscriberRequest
    ): SubscriberDto {
        return subscriberService.updateSubscriber(id, body).let {
            SubscriberDto.fromEntity(it)
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Subscriber deleted successfully.")
    fun deleteSubscriber(
        @PathVariable id: String
    ) = subscriberService.deleteSubscriberById(id)
}