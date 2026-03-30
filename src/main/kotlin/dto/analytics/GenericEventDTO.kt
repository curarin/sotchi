package app.sotchi.dto.analytics

import kotlin.time.Instant

/**
 * Use case: Event for analytics purposes which is published to some sort of messaging queue downstream.
 */
data class GenericEventDTO(
    val eventName: String = "undefined",
    val eventVersion: Int = 1,
    val userId: Int,
    val createdAtDt: Instant,
    val env: String = "dev",
)
