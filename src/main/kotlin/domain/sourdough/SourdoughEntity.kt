package app.sotchi.domain.sourdough

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.user.UserEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SourdoughEntity(
    val id: Int? = null,
    val user: UserEntity? = null,
    val feedState: SourdoughFeedStateEntity,
    val flourType: FlourTypeEntity,
    val sourdoughName: String,
    val createdAtDt: Instant,
    val nextStateChangeAtDt: Instant,
    val lastModifiedAtDt: Instant,
    val lastTimeFedDt: Instant,
    var deletedAtDt: Instant?
    )
