package app.sotchi.service

import app.sotchi.domain.exception.EmailAlreadyInUseException
import app.sotchi.domain.exception.UserNotFoundException
import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserLoginDTO
import app.sotchi.dto.user.UserProfileDTO
import app.sotchi.dto.user.UserReadDTO
import app.sotchi.dto.user.UserUpdateDTO
import app.sotchi.repository.UserRepository
import kotlin.time.Clock

class UserService(
    private val userRepository: UserRepository
) {
    // Wir brauchen: READ, CREATE, UPDATE, DELETE

    /**
     * User creates a new account.
     */
    fun create(dto: UserCreateDTO): UserProfileDTO {
        val existingUser = userRepository.findByEmail(dto.email)
        if (existingUser != null) {
            throw EmailAlreadyInUseException()
        }

        val now = Clock.System.now()

        val newUser = UserEntity(
            id = 0,
            name = dto.name,
            email = dto.email,
            createdAtDt = now,
            lastModifiedDt = now
        )

        val savedUser = userRepository.save(newUser)

        return UserProfileDTO(
            name = savedUser.name,
            email = savedUser.email,
            createdAtDt = savedUser.createdAtDt
        )
    }

    /**
     * User modifies their account.
     */
    fun update(userId: Int, dto: UserUpdateDTO): UserProfileDTO {
        val existingUser = userRepository.findById(userId) ?: throw UserNotFoundException()

        if (
            dto.email != null &&
            dto.email != existingUser.email
        ) {
            throw EmailAlreadyInUseException()
        }

        val updatedUser = existingUser.copy(
            name = dto.name ?: existingUser.name,
            email = dto.email ?: existingUser.email,
            lastModifiedDt = Clock.System.now()
        )

        userRepository.save(updatedUser)

        return UserProfileDTO(
            email = updatedUser.email,
            name = updatedUser.name,
            createdAtDt = updatedUser.createdAtDt
        )
    }

    /**
     * User deletes their account.
     */
    fun delete(userId: Int): Boolean {
        return userRepository.deleteById(userId)
    }


    /**
     * Login an existing user.
     */
    fun login(dto: UserLoginDTO): UserProfileDTO {
        val loggedInUser = userRepository.findByEmail(dto.email) ?: throw UserNotFoundException()
        return UserProfileDTO(
            name = loggedInUser.name,
            email = loggedInUser.email,
            createdAtDt = loggedInUser.createdAtDt
        )
    }

    /**
     * Reads data from an existing account.
     */
    fun read(dto: UserReadDTO): UserProfileDTO {
        val readUser = userRepository.findById(dto.id) ?: throw UserNotFoundException()
        return UserProfileDTO(
            name = readUser.name,
            email = readUser.email,
            createdAtDt = readUser.createdAtDt
        )
    }
}