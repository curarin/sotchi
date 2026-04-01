package app.sotchi.dto.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CreateSourdoughDTO(
    val name: String,
    val lastTimeFedDt: Instant,
    val flourType: FlourTypeEntity
)