package app.sotchi.dto.user

import app.sotchi.domain.generic.UserRole

/**
 * Use case: Returned to client after successful log-in.
 */
data class UserAuthenticationDTO(
    val id: Int,
    val role: UserRole
)
