package app.sotchi.repository

import app.sotchi.domain.user.UserEntity

class UserRepositoryInMemoryImpl : UserRepository {
    private val users = mutableMapOf<Int, UserEntity>()
    private var nextId = 1

    override fun findById(id: Int): UserEntity? {
        return users[id]
    }

    override fun findByEmail(email: String): UserEntity? {
        return users.values.firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    override fun save(user: UserEntity): UserEntity {
        val userToSave = if (user.id == 0) {
            user.copy(id = nextId++)
        } else {
            user
        }
        users[userToSave.id] = userToSave
        return userToSave
    }

    override fun deleteById(id: Int): Boolean {
        return users.remove(id) != null
    }
}