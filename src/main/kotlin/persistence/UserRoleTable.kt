package app.sotchi.persistence

import app.sotchi.domain.generic.UserRole
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object UserRoleTable : IntIdTable("user_role") {
    val role = enumerationByName<UserRole>("role", 20).uniqueIndex()
}