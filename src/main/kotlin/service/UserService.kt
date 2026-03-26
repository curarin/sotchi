package app.sotchi.service

import app.sotchi.domain.exception.EmailAlreadyInUseException
import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserUpdateDTO
import app.sotchi.repository.UserRepository

class UserService(
    private val userRepository: UserRepository
) {

    /**
     * User creates a new account.
     */
    fun create(dto: UserCreateDTO): UserEntity {
        // Check if user already exists
        if (dto.email.equals(userRepository.findByEmail(dto.email))) {
            throw EmailAlreadyInUseException()
        }
        return userRepository.create(dto)
    }

    /**
     * User modifies their account.
     */
    fun update(userId: Int, dto: UserUpdateDTO): UserEntity {
        val existingUser = userRepository.findById(userId)

        if (
            dto.email != null &&
            dto.email != existingUser.email
        ) {
            throw EmailAlreadyInUseException()
        }

        return userRepository.update(
            id = userId,
            dto = dto
        )
    }

    /**
     * User deletes their account.
     */
    fun delete(userId: Int): Boolean {
        return userRepository.delete(userId)
    }

    /**
     * Find a user by their email address.
     */
    fun findByEmail(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }
}