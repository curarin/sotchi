package app.sotchi.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateDTO(
    val name: String? = null,
    val email: String? = null
)
