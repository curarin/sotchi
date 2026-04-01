package app.sotchi.dto.user

import app.sotchi.domain.generic.UserRole

data class UserAuthenticationDTO(
    val id: Int,
    val role: UserRole
)
