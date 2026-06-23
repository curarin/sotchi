package app.sotchi.dto.analytics

import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Concrete implementation of a analytics event
 * for whenever a user was successfully deleted
 */
data class UserDeletedEvent(
    val occuredAtDt: Instant = Clock.System.now(),
    val env: String = "dev",
    val eventName: String = "userDeleted",
    val eventVersion: Int = 1,
    val userId: Int
) : DomainEvent