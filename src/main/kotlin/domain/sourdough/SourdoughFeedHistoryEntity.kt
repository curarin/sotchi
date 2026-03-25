package app.sotchi.domain.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import java.time.LocalDateTime

data class SourdoughFeedHistoryEntity(
    val id: Int,
    val sourdoughEntity: SourdoughEntity,
    val flourTypeEntity: FlourTypeEntity,
    val fedAtDt: LocalDateTime,
    val growingTimeOverAtDt: LocalDateTime,
)
