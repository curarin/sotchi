package app.sotchi.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserModifyDTO(
    val name: String? = null,
    val email: String? = null
)
