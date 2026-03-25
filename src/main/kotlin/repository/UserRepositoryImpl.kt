package app.sotchi.repository

import app.sotchi.domain.user.UserEntity
import kotlinx.datetime.Clock
import java.util.concurrent.atomic.AtomicInteger

class UserRepositoryImpl : UserRepository {
    private val users = mutableListOf<UserEntity>()
    private val idGenerator = AtomicInteger(1)
    override fun findById(id: Int): UserEntity {
        val user = UserEntity(
            id = idGenerator.getAndIncrement(),
            name = "User 1",
            email = "test@mail.com",
            createdAtDt = Clock.System.now(),
            lastModifiedDt = Clock.System.now()
        )
        return user
    }

    override fun findByEmail(email: String): UserEntity? {
        TODO("Not yet implemented")
    }

    override fun save(user: UserEntity): UserEntity {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}