package app.sotchi.dto.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import kotlinx.serialization.Serializable
import kotlinx.datetime.*

@Serializable
data class CreateSourdoughDTO(
    val name: String,
    val userId: Int,
    val lastTimeFedDt: Instant,
    val flourType: FlourTypeEntity
)