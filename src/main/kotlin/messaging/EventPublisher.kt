package app.sotchi.messaging

import app.sotchi.dto.analytics.DomainEvent

/**
 * Publisher / Subject interface for further implementation
 * of Observer Pattern - used to handle all relevant subscribers
 * and publish / notify subscribed Observers
 */
interface EventPublisher {
    fun subscribe(subscriber: EventSubscriber)
    fun unsubscribe(subscriber: EventSubscriber)
    fun publish(event: DomainEvent)
}