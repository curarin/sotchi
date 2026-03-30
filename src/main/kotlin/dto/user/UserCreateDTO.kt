package app.sotchi.dto.user

import kotlinx.serialization.Serializable

/**
 * Use case: Incoming request when users create a new account.
 */
@Serializable
data class UserCreateDTO(
    val name: String,
    val email: String
)
