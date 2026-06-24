package app.sotchi.domain.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import app.sotchi.domain.user.UserEntity
import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SourdoughEntity(
    val id: Int? = null,
    val user: UserEntity,
    val flourType: FlourTypeEntity,
    val liquidType: LiquidTypeEntity,
    val sourdoughName: String,
    val createdAtDt: Instant,
    val lastModifiedAtDt: Instant,
    val lastFedAtDt: Instant? = null
)
