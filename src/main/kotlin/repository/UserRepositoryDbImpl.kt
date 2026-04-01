package app.sotchi.repository

import app.sotchi.domain.user.UserEntity
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import io.ktor.util.logging.*

internal val LOGGER = KtorSimpleLogger("com.example.RequestTracePlugin")
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

        LOGGER.info("User found by id: $id")

        return UserEntity(
            id = row[UserTable.id].value,
            name = row[UserTable.name],
            email = row[UserTable.email],
            password = row[UserTable.password],
            createdAtDt = row[UserTable.createdAtDt],
            role = row[UserTable.role],
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

        LOGGER.info("User found by email: $email")

        return UserEntity(
            id = row[UserTable.id].value,
            name = row[UserTable.name],
            email = row[UserTable.email],
            password = row[UserTable.password],
            createdAtDt = row[UserTable.createdAtDt],
            role = row[UserTable.role],
            lastModifiedDt = row[UserTable.lastModifiedDt]
        )
    }

    override fun save(user: UserEntity): UserEntity {
        // ToDo: Kompletter Yolohaufen diese Implementierung. Das muss noch eleganter gehen. Aber es funzt zumindest mal.
        val saveRow = transaction {
            if (user.id == 0) {
                val inserted = UserTable.insert {
                    it[name] = user.name
                    it[email] = user.email
                    it[password] = user.password
                    it[createdAtDt] = user.createdAtDt
                    it[role] = user.role
                    it[lastModifiedDt] = user.lastModifiedDt
                }

                val generatedId = inserted[UserTable.id].value

                LOGGER.info("New user inserted with id: $generatedId")

                UserTable
                    .selectAll()
                    .where { UserTable.id eq generatedId }
                    .single()

            } else {
                UserTable.update({ UserTable.id eq user.id }) {
                    it[name] = user.name
                    it[email] = user.email
                    it[password] = user.password
                    it[lastModifiedDt] = user.lastModifiedDt
                }

                LOGGER.info("Existing user updated with id ${user.id}")
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
            password = saveRow[UserTable.password],
            createdAtDt = saveRow[UserTable.createdAtDt],
            role = saveRow[UserTable.role],
            lastModifiedDt = saveRow[UserTable.lastModifiedDt]
        )
    }

    override fun deleteById(id: Int): Boolean {
        val deletedCount = transaction {
            UserTable.deleteWhere { UserTable.id eq id }
        }
        if (deletedCount > 0) {
            LOGGER.info("User deleted with id: $id")
            return true
        } else {
            LOGGER.warn("Tried to delete user with id $id - no rows deleted.")
            return false
        }
    }
}