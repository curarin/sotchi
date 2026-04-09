package app.sotchi.domain.user

import app.sotchi.domain.generic.UserRole
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class UserEntity(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val createdAtDt: Instant,
    val lastModifiedDt: Instant,
    val role: UserRole = UserRole.STANDARD,
    val isActivated: Boolean = false,
    val activatedAtDt: Instant? = null,
    val activationToken: String? = null,
    val activationTokenValidUntil: Instant? = null
)
