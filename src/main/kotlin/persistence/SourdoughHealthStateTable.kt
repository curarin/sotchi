package app.sotchi.persistence

import app.sotchi.domain.generic.SourdoughHealthState
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object SourdoughHealthStateTable : IntIdTable("sourdough_state") {
    val sourdoughHealthStateName = enumerationByName<SourdoughHealthState>(name = "sourdough_health_state_name", 255).uniqueIndex()
}