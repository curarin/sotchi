package app.sotchi.persistence

import app.sotchi.domain.generic.UserRole
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp


object UserTable : IntIdTable("user") {
    val name = varchar("name", 50)
    val email = varchar("email", 64).uniqueIndex()
    val password = varchar("password", 256)
    val createdAtDt = timestamp("created_at_dt")
    val lastModifiedDt = timestamp("last_modified_dt")
    val role = enumerationByName<UserRole>("role", 20)
}