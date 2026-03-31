package app.sotchi.repository

import app.sotchi.domain.user.UserEntity
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

/**
 * Implementation of Database persistence setup.
 */
class UserRepositoryDbImpl : UserRepository {
    override fun findById(id: Int): UserEntity? {
        val row = transaction {
            UserTable
                .selectAll()
                .where { UserTable.id eq id }
                .singleOrNull()
        } ?: return null

        return UserEntity(
            id = row[UserTable.id].value,
            name = row[UserTable.name],
            email = row[UserTable.email],
            createdAtDt = row[UserTable.createdAtDt],
            lastModifiedDt = row[UserTable.lastModifiedDt]
        )
    }

    override fun findByEmail(email: String): UserEntity? {
        val row = transaction {
            UserTable
                .selectAll()
                .where { UserTable.email eq email }
                .singleOrNull()
        } ?: return null

        return UserEntity(
            id = row[UserTable.id].value,
            name = row[UserTable.name],
            email = row[UserTable.email],
            createdAtDt = row[UserTable.createdAtDt],
            lastModifiedDt = row[UserTable.lastModifiedDt]
        )
    }

    override fun save(user: UserEntity): UserEntity {
        val saveRow = transaction {
            if (user.id == 0) {
                val inserted = UserTable.insert {
                    it[name] = user.name
                    it[email] = user.email
                    it[createdAtDt] = user.createdAtDt
                    it[lastModifiedDt] = user.lastModifiedDt
                }

                val generatedId = inserted[UserTable.id].value

                UserTable
                    .selectAll()
                    .where { UserTable.id eq generatedId }
                    .single()

            } else {
                UserTable.update({ UserTable.id eq user.id }) {
                    it[name] = user.name
                    it[email] = user.email
                    it[lastModifiedDt] = user.lastModifiedDt
                }
                UserTable
                    .selectAll()
                    .where { UserTable.id eq user.id }
                    .single()
            }
        }
        return UserEntity(
            id = saveRow[UserTable.id].value,
            name = saveRow[UserTable.name],
            email = saveRow[UserTable.email],
            createdAtDt = saveRow[UserTable.createdAtDt],
            lastModifiedDt = saveRow[UserTable.lastModifiedDt]
        )
    }

    override fun deleteById(id: Int): Boolean {
        val deletedCount = transaction {
            UserTable.deleteWhere { UserTable.id eq id }
        }
        return deletedCount > 0
    }
}