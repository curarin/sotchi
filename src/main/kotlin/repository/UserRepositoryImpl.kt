package app.sotchi.repository

import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.UserCreateDTO
import kotlinx.datetime.Clock

class UserRepositoryImpl : UserRepository {
    private val users = mutableMapOf<Int, UserEntity>()
    private var nextId = 1

    override fun findById(id: Int): UserEntity? = users[id]
    override fun findByEmail(email: String): UserEntity? =
        users.values.firstOrNull { it.email.equals(email, ignoreCase = true) }

    override fun create(dto: UserCreateDTO): UserEntity {
        val currentDateTime = Clock.System.now()
        val user = UserEntity(
            id = nextId++,
            name = dto.name,
            email = dto.email,
            createdAtDt = currentDateTime,
            lastModifiedDt = currentDateTime
        )
        users[user.id] = user
        return user
    }

    override fun update(
        id: Int,
        name: String?,
        email: String?
    ): UserEntity? {
        val existingUser = users[id] ?: return null

        val updatedUser = existingUser.copy(
            name = name ?: existingUser.name,
            email = email ?: existingUser.email,
            lastModifiedDt = Clock.System.now()
        )

        users[id] = updatedUser
        return updatedUser
    }

    override fun delete(id: Int): Boolean = users.remove(id) != null
}