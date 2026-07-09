package app.sotchi.dto.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import kotlinx.serialization.Serializable

/**
 * Use Case: Incoming request when a user modifies an existing sourdough
 */
@Serializable
data class SourdoughUpdateDTO(
    val id: Int,
    val name: String? = null,
    val flourType: FlourTypeEntity? = null,
    val liquidType: LiquidTypeEntity? = null,
)