package app.sotchi.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateDTO(
    val name: String,
    val email: String
)
