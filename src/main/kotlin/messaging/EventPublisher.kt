package app.sotchi.messaging

import app.sotchi.dto.analytics.GenericEventDTO

interface EventPublisher {
    fun publishUserEvent(eventName: String, userId: Int): GenericEventDTO
}