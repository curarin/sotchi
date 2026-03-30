package app.sotchi.repository
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.*

const val MAX_VARCHAR_LENGTH = 50

object UserTable : IntIdTable() {
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val email = varchar("email", MAX_VARCHAR_LENGTH)
    val createdAtDt = timestamp("created_at_dt")
    val lastModifiedDt = timestamp("last_modified_dt")
}