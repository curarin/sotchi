package app.sotchi.service

import app.sotchi.domain.sourdough.SourdoughEntity
import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.repository.UserRepository

class UserService(
    private val userRepository: UserRepository
) {

    /**
     * User creates a new account.
     */
    fun create(dto: UserCreateDTO): UserEntity {
        return userRepository.create(dto)
    }


    /**
     * User logs into their account.
     */
    fun login(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }

    /**
     * User modifies their account.
     */
    fun modifyUser(
        userId: Int,
        name: String?,
        email: String?
    ): UserEntity? {
        return userRepository.update(
            id = userId,
            name = name,
            email = email
        )
    }

    /**
     * User deletes their account.
     */
    fun deleteUser(userId: Int): Boolean {
        return userRepository.delete(userId)
    }

    /**
     * Find a user by their ID.
     */
    fun getUserById(userId: Int): UserEntity? {
        return userRepository.findById(userId)
    }
}