package app.sotchi.messaging

import app.sotchi.dto.analytics.DomainEvent

interface EventSubscriber {
    fun handle(event: DomainEvent)
}