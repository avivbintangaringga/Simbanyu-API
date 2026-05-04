package com.avivba.simbanyu.api.utils

import com.github.f4b6a3.ulid.UlidCreator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.generator.BeforeExecutionGenerator
import org.hibernate.generator.EventType
import java.util.*

class UlidGenerator : BeforeExecutionGenerator {
    override fun generate(
        session: SharedSessionContractImplementor?,
        owner: Any?,
        currentValue: Any?,
        eventType: EventType?
    ): Any? {
        return UlidCreator.getUlid()
    }

    override fun getEventTypes(): EnumSet<EventType?>? {
        return EnumSet.of(EventType.INSERT)
    }
}