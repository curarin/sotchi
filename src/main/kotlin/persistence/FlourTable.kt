package app.sotchi.persistence

import app.sotchi.domain.generic.FlourTypeEntity
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object FlourTable : IntIdTable("flour") {
    val flourType = enumerationByName<FlourTypeEntity>("flour_type", 255).uniqueIndex()
}