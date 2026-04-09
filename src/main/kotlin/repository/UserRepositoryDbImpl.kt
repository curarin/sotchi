package app.sotchi.repository

import app.sotchi.domain.user.UserEntity
import app.sotchi.persistence.UserActivationTable
import app.sotchi.persistence.UserRoleTable
import app.sotchi.persistence.UserTable
import io.ktor.util.logging.*
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

internal val LOGGER = KtorSimpleLogger("com.example.RequestTracePlugin")

/**
 * Implementation of Database persistence setup.
 */
class UserRepositoryDbImpl : UserRepository {
    override fun findById(id: Int): UserEntity? {
        val row = transaction {
            UserTable.join(
                UserRoleTable,
                JoinType.INNER,
                additionalConstraint = { UserTable.roleId eq UserRoleTable.id }).join(
                UserActivationTable,
                JoinType.INNER,
                additionalConstraint = { UserTable.id eq UserActivationTable.userId }).selectAll()
                .where { UserTable.id eq id }.singleOrNull()
        } ?: return null

        LOGGER.info("User found by id: $id")

        return UserEntity(
            id = row[UserTable.id].value,
            name = row[UserTable.name],
            email = row[UserTable.email],
            password = row[UserTable.password],
            createdAtDt = row[UserTable.createdAtDt],
            role = row[UserRoleTable.role],
            lastModifiedDt = row[UserTable.lastModifiedDt],
            isActivated = row[UserActivationTable.isActivated],
            activatedAtDt = row[UserActivationTable.activatedAtDt]
        )
    }

    override fun findByEmail(email: String): UserEntity? {
        val row = transaction {
            UserTable.join(
                UserRoleTable,
                JoinType.INNER,
                additionalConstraint = { UserTable.roleId eq UserRoleTable.id }).join(
                UserActivationTable,
                JoinType.INNER,
                additionalConstraint = { UserTable.id eq UserActivationTable.userId }).selectAll()
                .where { UserTable.email eq email }.singleOrNull()
        } ?: return null

        LOGGER.info("User found by email: $email")

        return UserEntity(
            id = row[UserTable.id].value,
            name = row[UserTable.name],
            email = row[UserTable.email],
            password = row[UserTable.password],
            createdAtDt = row[UserTable.createdAtDt],
            role = row[UserRoleTable.role],
            lastModifiedDt = row[UserTable.lastModifiedDt],
            isActivated = row[UserActivationTable.isActivated],
            activatedAtDt = row[UserActivationTable.activatedAtDt]
        )
    }

    override fun save(user: UserEntity): UserEntity {
        // ToDo: Kompletter Yolohaufen diese Implementierung. Das muss noch eleganter gehen. Aber es funzt zumindest mal.
        val saveRow = transaction {
            // User does not exist yet - we create a new one
            if (user.id == 0) {

                // We try to find the user role with which the user was created
                // In case the role does exist already we return the existing role id
                // In case the role is new we insert it and return the id
                val roleInserted =
                    UserRoleTable.select(UserRoleTable.id).where { UserRoleTable.role eq user.role }.singleOrNull()
                        ?.get(UserRoleTable.id) ?: UserRoleTable.insertAndGetId {
                        it[role] = user.role
                    }

                val userInserted = UserTable.insert {
                    it[name] = user.name
                    it[email] = user.email
                    it[password] = user.password
                    it[createdAtDt] = user.createdAtDt
                    it[lastModifiedDt] = user.lastModifiedDt
                    it[roleId] = roleInserted
                }

                val activationInserted = UserActivationTable.insert {
                    it[UserActivationTable.userId] = userInserted[UserTable.id].value
                    it[isActivated] = false
                    it[createdAtDt] = user.createdAtDt
                    it[activationToken] = requireNotNull(user.activationToken)
                    it[activationTokenValidUntil] = requireNotNull(user.activationTokenValidUntil)
                }

                val generatedId = userInserted[UserTable.id].value
                val activationToken = activationInserted[UserActivationTable.activationToken]

                LOGGER.info("New user inserted with id: $generatedId - activation token: $activationToken - created role: $roleInserted")

                UserTable.join(
                    UserRoleTable, JoinType.INNER, additionalConstraint = { UserTable.roleId eq UserRoleTable.id })
                    .join(
                        UserActivationTable,
                        JoinType.INNER,
                        additionalConstraint = { UserTable.id eq UserActivationTable.userId }).selectAll()
                    .where { UserTable.id eq generatedId }.single()

            } else {
                // In case the role does exist already we return the existing role id
                // In case the role is new we insert it and return the id
                val roleInserted =
                    UserRoleTable.select(UserRoleTable.id).where { UserRoleTable.role eq user.role }.singleOrNull()
                        ?.get(UserRoleTable.id) ?: UserRoleTable.insertAndGetId {
                        it[role] = user.role
                    }

                UserTable.update({ UserTable.id eq user.id }) {
                    it[name] = user.name
                    it[email] = user.email
                    it[password] = user.password
                    it[lastModifiedDt] = user.lastModifiedDt
                    it[roleId] = roleInserted
                }

                UserActivationTable.update({ UserActivationTable.userId eq user.id }) {
                    it[isActivated] = user.isActivated
                    it[activatedAtDt] = user.activatedAtDt
                }

                LOGGER.info("Existing user updated with id ${user.id}")
                UserTable.join(
                    UserRoleTable, JoinType.INNER, additionalConstraint = { UserTable.roleId eq UserRoleTable.id })
                    .join(
                        UserActivationTable,
                        JoinType.INNER,
                        additionalConstraint = { UserTable.id eq UserActivationTable.userId }).selectAll()
                    .where { UserTable.id eq user.id }.single()
            }
        }
        return UserEntity(
            id = saveRow[UserTable.id].value,
            name = saveRow[UserTable.name],
            email = saveRow[UserTable.email],
            password = saveRow[UserTable.password],
            createdAtDt = saveRow[UserTable.createdAtDt],
            role = saveRow[UserRoleTable.role],
            lastModifiedDt = saveRow[UserTable.lastModifiedDt],
            isActivated = saveRow[UserActivationTable.isActivated],
            activatedAtDt = saveRow[UserActivationTable.activatedAtDt]
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