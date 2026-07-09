package app.sotchi.dto.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import app.sotchi.domain.generic.SourdoughHealthState
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Use Case: Outgoing response for when a user requested one / many sourdough(s)
 * This shall restrict the SourdoughEntity
 */
@Serializable
data class SourdoughProfileDTO(
    val id: Int,
    val flourType: FlourTypeEntity,
    val liquidType: LiquidTypeEntity,
    val sourdoughName: String,
    val createdAtDt: Instant,
    val lastModifiedAtDt: Instant,
    val healthState: SourdoughHealthState,
    val lastFedAtDt: Instant? = null
)