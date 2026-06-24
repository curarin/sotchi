package app.sotchi.domain.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import app.sotchi.domain.user.UserEntity
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SourdoughEntity(
    val id: Int? = null,
    val userId: Int,
    val flourType: FlourTypeEntity,
    val liquidType: LiquidTypeEntity,
    val sourdoughName: String,
    val createdAtDt: Instant,
    val lastModifiedAtDt: Instant,
    val lastFedAtDt: Instant? = null
)
