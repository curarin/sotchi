package app.sotchi.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginDTO(
    val email: String
)
