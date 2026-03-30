package app.sotchi.repository

import app.sotchi.domain.user.UserEntity
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select

class UserRepositoryDbImpl : UserRepository {
    override fun findById(id: Int): UserEntity? {
        val row = UserTable
            .select(
                UserTable.id,
                UserTable.email,
                UserTable.name,
                UserTable.createdAtDt,
                UserTable.lastModifiedDt
            )
            .where { UserTable.id eq id }
            .singleOrNull() ?: return null

        return UserEntity(
            id = row[UserTable.id].value,
            name = row[UserTable.name],
            email = row[UserTable.email],
            createdAtDt = row[UserTable.createdAtDt],
            lastModifiedDt = row[UserTable.lastModifiedDt]
        )
    }

    override fun findByEmail(email: String): UserEntity? {
        val row = UserTable
            .select(
                UserTable.id,
                UserTable.email,
                UserTable.name,
                UserTable.createdAtDt,
                UserTable.lastModifiedDt
            )
            .where { UserTable.email eq email }
            .singleOrNull() ?: return null

        return UserEntity(
            id = row[UserTable.id].value,
            name = row[UserTable.name],
            email = row[UserTable.email],
            createdAtDt = row[UserTable.createdAtDt],
            lastModifiedDt = row[UserTable.lastModifiedDt]
        )
    }

    override fun save(user: UserEntity): UserEntity {
        val user = UserTable.insert {
            it[name] = user.name
        }
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int): Boolean {
        TODO("Not yet implemented")
    }
}