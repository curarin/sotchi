package app.sotchi.dto.user

import kotlinx.serialization.Serializable

/**
 * Use case: Users are updating their profile.
 */
@Serializable
data class UserUpdateDTO(
    val id: Int,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null
)
