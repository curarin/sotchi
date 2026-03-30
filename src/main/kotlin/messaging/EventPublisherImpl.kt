package app.sotchi.messaging

import app.sotchi.dto.analytics.GenericEventDTO
import kotlin.time.Clock

/**
 * Implementation for publishing our analytic events.
 */
class EventPublisherImpl : EventPublisher {
    private val analyticEvents = mutableListOf<GenericEventDTO>()

    override fun publishUserEvent(eventName: String, userId: Int): GenericEventDTO {
        val genericEventDTO = GenericEventDTO(
            eventName = eventName,
            userId = userId,
            createdAtDt = Clock.System.now(),
        )
        analyticEvents.add(genericEventDTO)
        return genericEventDTO
    }
}