package app.sotchi.persistence

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp

object SourdoughFeedLogTable : IntIdTable("sourdough_feed_log") {
    val sourdoughId = optReference("sourdough_id", SourdoughTable.id, ReferenceOption.CASCADE)
    val sourdoughFedAtDt = timestamp("sourdough_fed_at_dt")
}