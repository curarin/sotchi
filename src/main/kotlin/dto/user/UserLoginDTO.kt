package app.sotchi.dto.user

import kotlinx.serialization.Serializable

/**
 * Use case: Incoming request when a user tries to log-in.
 */
@Serializable
data class UserLoginDTO(
    val email: String,
    val password: String
)
