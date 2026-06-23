package app.sotchi.messaging

import app.sotchi.dto.analytics.DomainEvent

/**
 * Concrete implementation of an event publisher for default publishing use case
 */
class DefaultEventPublisher : EventPublisher {
    private val subscribers = mutableListOf<EventSubscriber>()

    override fun subscribe(subscriber: EventSubscriber) {
        subscribers.add(subscriber)
    }

    override fun unsubscribe(subscriber: EventSubscriber) {
        subscribers.remove(subscriber)
    }

    override fun publish(event: DomainEvent) {
        subscribers.forEach { subscriber ->
            subscriber.handle(event)
        }
    }
}