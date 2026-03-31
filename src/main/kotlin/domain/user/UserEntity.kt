package app.sotchi.domain.user

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
)
