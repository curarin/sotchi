package app.sotchi.persistence

import app.sotchi.domain.generic.LiquidTypeEntity
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object LiquidTable : IntIdTable("liquid") {
    val liquidType = enumerationByName<LiquidTypeEntity>("liquid", 255).uniqueIndex()
}