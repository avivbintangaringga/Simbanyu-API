package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.features.bill.model.Bill
import com.avivba.simbanyu.api.features.bill.model.BillStatus
import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.util.UUID

class BillSpecs {
    companion object {
        fun filterBy(subscriberId: String?, activeOnly: Boolean?): Specification<Bill> {
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

                activeOnly?.let {
                    if (activeOnly) {
                        predicates.add(
                            cb.equal(
                                root.get<BillStatus>("status"),
                                BillStatus.UNPAID
                            )
                        )
                    }
                }

                cb.and(*predicates.toTypedArray())
            }
        }
    }
}