package app.sotchi.dto.user

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Use case: Returns the minimum needed user data which shall be used as return to API frontend.
 */
@Serializable
data class UserProfileDTO(
    val name: String,
    val email: String,
    val createdAtDt: Instant,
    val isActivated: Boolean,
    val activatedAtDt: Instant? = null
)
