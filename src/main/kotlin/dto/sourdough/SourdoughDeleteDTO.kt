package app.sotchi.dto.sourdough

import kotlinx.serialization.Serializable

/**
 * Use Case: Incoming request when a user deletes an existing sourdough
 */
@Serializable
data class SourdoughDeleteDTO(
    val id: Int,
)
