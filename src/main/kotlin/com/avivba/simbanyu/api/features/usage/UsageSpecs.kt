package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import com.avivba.simbanyu.api.features.usage.model.Usage
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.util.UUID

class UsageSpecs {
    companion object {
        fun filterBy(
            subscriberId: String?,
            includeInitial: Boolean = false
        ): Specification<Usage> {
            return Specification { root, _, cb ->
                val predicates = mutableListOf<Predicate>()

                subscriberId?.let {
                    predicates.add(
                        cb.equal(
                            root.get<Subscriber>("subscriber").get<UUID>("id"),
                            UUID.fromString(subscriberId)
                        )
                    )
                }

                if (!includeInitial) {
                    predicates.add(
                        cb.equal(
                            root.get<Boolean>("isInitial"),
                            false
                        )
                    )
                }

                cb.and(*predicates.toTypedArray())
            }
        }
    }
}