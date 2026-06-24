package app.sotchi.dto.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import kotlinx.serialization.Serializable

/**
 * Use Case: Incoming request when a user creates a fresh new sourdough
 */
@Serializable
data class SourdoughCreateDTO(
    val name: String,
    val flourType: FlourTypeEntity,
    val liquidType: LiquidTypeEntity,
)