package app.sotchi.dto.sourdough

import kotlinx.serialization.Serializable

/**
 * Use Case: Incoming request when a user wants to read one specific sourdough
 */
@Serializable
data class SourdoughReadDTO(
    val id: Int,
)