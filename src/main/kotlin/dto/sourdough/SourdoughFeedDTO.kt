package app.sotchi.dto.sourdough

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Use Case: Incoming request when a user feeds an existing sourdough
 */
@Serializable
data class SourdoughFeedDTO(
    val id: Int,
    val fedAtDt: Instant = Clock.System.now(),
)