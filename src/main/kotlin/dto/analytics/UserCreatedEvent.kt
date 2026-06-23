package app.sotchi.dto.analytics

import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Concrete implementation of a analytics event
 * for whenever a user is successfully created
 */
data class UserCreatedEvent(
    val occuredAtDt: Instant = Clock.System.now(),
    val env: String = "dev",
    val eventName: String = "userCreated",
    val eventVersion: Int = 1,
    val userId: Int
) : DomainEvent