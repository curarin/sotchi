package app.sotchi.persistence

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp

object SourdoughTable : IntIdTable("sourdough") {
    val name = varchar("name", 255)
    val liquidId = optReference("liquid_id", LiquidTable.id, ReferenceOption.CASCADE)
    val flourId = optReference("flour_id", FlourTable.id, ReferenceOption.CASCADE)
    val userId = optReference("user_id", UserTable.id, ReferenceOption.CASCADE)
    val lastModifiedDt = timestamp("last_modified_dt")
    val createdAtDt = timestamp("created_at_dt")
    val healthStateId = optReference("sourdough_health_state_id", SourdoughHealthStateTable.id, ReferenceOption.CASCADE)
}