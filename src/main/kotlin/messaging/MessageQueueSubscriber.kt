package app.sotchi.messaging

import app.sotchi.dto.analytics.DomainEvent
import io.ktor.util.logging.KtorSimpleLogger

internal val LOGGER = KtorSimpleLogger("com.example.RequestTracePlugin")

class MessageQueueSubscriber : EventSubscriber{
    override fun handle(event: DomainEvent) {
        LOGGER.info("[MessageQueueSubscriber]: New message pushed: $event")
        TODO("Not yet implemented")
    }
}