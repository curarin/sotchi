package app.sotchi.dto.user

import kotlinx.serialization.Serializable

/**
 * Use case: Incoming request when user try to get data based on their ID (GET request).
 */
@Serializable
data class UserReadDTO(
    val id: Int
)
