package app.sotchi.domain.user

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id: Int,
    val name: String,
    val email: String,
    val createdAtDt: Instant,
    val lastModifiedDt: Instant,
)
