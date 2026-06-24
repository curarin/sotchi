package app.sotchi.dto.analytics

import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Concrete implementation of a analytics event
 * for whenever a user is successfully updated
 */
data class UserUpdatedEvent(
    val occurredAtDt: Instant = Clock.System.now(),
    val env: String = "dev",
    val eventName: String = "userUpdated",
    val eventVersion: Int = 1,
    val userId: Int
) : DomainEvent