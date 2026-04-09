package app.sotchi.persistence

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp

object UserActivationTable : IntIdTable("user_activation") {
    val userId = optReference("user_id", UserTable.id, ReferenceOption.CASCADE)
    val isActivated = bool("activated").default(false)
    val createdAtDt = timestamp("created_at_dt")
    val activatedAtDt = timestamp("activated_at_dt").nullable()
    val activationToken = varchar("activation_token", 256).uniqueIndex()
    val activationTokenValidUntil = timestamp("activation_token_valid_until")
}