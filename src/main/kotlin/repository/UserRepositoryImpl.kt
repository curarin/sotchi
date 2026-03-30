package app.sotchi.repository

import app.sotchi.domain.exception.UserNotFoundException
import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.*
import kotlinx.datetime.Clock

class UserRepositoryImpl : UserRepository {
    private val users = mutableMapOf<Int, UserEntity>()
    private var nextId = 1

    override fun findById(id: Int): UserEntity {
        return users[id] ?: throw UserNotFoundException()
    }

    override fun findByEmail(email: String): UserEntity {
        return users.values.firstOrNull { it.email.equals(email, ignoreCase = true) } ?: throw UserNotFoundException()
    }

    override fun login(dto: UserLoginDTO): UserProfileDTO {
        val loggedInUser = findByEmail(dto.email)
        return UserProfileDTO(
            name = loggedInUser.name,
            email = loggedInUser.email,
            createdAtDt = loggedInUser.createdAtDt
        )
    }

    override fun read(dto: UserReadDTO): UserProfileDTO {
        val existingUser = findById(dto.id)
        return UserProfileDTO(
            name = existingUser.name,
            email = existingUser.email,
            createdAtDt = existingUser.createdAtDt
        )
    }

    override fun create(dto: UserCreateDTO): UserProfileDTO {
        val currentDateTime = Clock.System.now()
        val user = UserEntity(
            id = nextId++,
            name = dto.name,
            email = dto.email,
            createdAtDt = currentDateTime,
            lastModifiedDt = currentDateTime
        )
        users[user.id] = user
        return UserProfileDTO(
            name = user.name,
            email = user.email,
            createdAtDt = user.createdAtDt
        )
    }

    override fun update(id: Int, dto: UserUpdateDTO): UserProfileDTO {
        val existingUser = users[id] ?: throw UserNotFoundException()

        val updatedUser = existingUser.copy(
            name = dto.name ?: existingUser.name,
            email = dto.email ?: existingUser.email,
            lastModifiedDt = Clock.System.now()
        )

        users[id] = updatedUser
        return UserProfileDTO(
            name = updatedUser.name,
            email = updatedUser.email,
            createdAtDt = updatedUser.createdAtDt
        )
    }

    override fun delete(id: Int): Boolean {
        val existingUser = users[id] ?: throw UserNotFoundException()
        return users.remove(existingUser.id) != null
    }
}