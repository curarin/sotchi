package app.sotchi.dto.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity

/**
 * Use Case: Incoming request when a user modifies an existing sourdough
 */
data class SourdoughUpdateDTO(
    val id: Int,
    val name: String? = null,
    val flourType: FlourTypeEntity? = null,
    val liquidType: LiquidTypeEntity? = null,
)