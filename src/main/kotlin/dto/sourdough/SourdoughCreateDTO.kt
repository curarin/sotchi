package app.sotchi.dto.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import app.sotchi.domain.generic.SourdoughHealthState
import kotlinx.serialization.Serializable

/**
 * Use Case: Incoming request when a user creates a fresh new sourdough
 */
@Serializable
data class SourdoughCreateDTO(
    val id: Int = 0,
    val name: String,
    val flourType: FlourTypeEntity,
    val liquidType: LiquidTypeEntity,
    val healthState: SourdoughHealthState
)