package app.sotchi.dto.analytics

import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Concrete implementation of a analytics event
 * for whenever a sourdough is fed
 */
data class SourdoughFedEvent(
    val occurredAtDt: Instant = Clock.System.now(),
    val env: String = "dev",
    val eventName: String = "sourdoughFed",
    val eventVersion: Int = 1,
    val userId: Int,
    val sourdoughId: Int
) : DomainEvent