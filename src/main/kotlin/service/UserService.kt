package app.sotchi.service

import app.sotchi.domain.exception.EmailAlreadyInUseException
import app.sotchi.domain.exception.UserNotAuthenticated
import app.sotchi.domain.exception.UserNotFoundException
import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.*
import app.sotchi.repository.UserRepository
import app.sotchi.security.Authentication
import kotlin.time.Clock
import io.ktor.util.logging.*

internal val LOGGER = KtorSimpleLogger("com.example.RequestTracePlugin")

class UserService(
    private val userRepository: UserRepository
) {
    /**
     * User creates a new account.
     */
    fun create(dto: UserCreateDTO): UserProfileDTO {
        val existingUser = userRepository.findByEmail(dto.email)
        if (existingUser != null) {
            LOGGER.warn("[create] User with email ${dto.email} already exists.")
            throw EmailAlreadyInUseException()
        }

        val now = Clock.System.now()

        val newUser = UserEntity(
            id = 0,
            name = dto.name,
            email = dto.email.trim().lowercase(),
            password = Authentication().hashPassword(dto.password.toCharArray()),
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

        if (dto.password != null) {
            val hashedPassword = Authentication().hashPassword(dto.password.toCharArray())
            val updatedUser = existingUser.copy(
                name = dto.name ?: existingUser.name,
                email = dto.email?.trim()?.lowercase() ?: existingUser.email,
                password = hashedPassword,
                lastModifiedDt = Clock.System.now()
            )
            userRepository.save(updatedUser)

            return UserProfileDTO(
                email = updatedUser.email,
                name = updatedUser.name,
                createdAtDt = updatedUser.createdAtDt
            )
        } else {
            val updatedUser = existingUser.copy(
                name = dto.name ?: existingUser.name,
                email = dto.email?.trim()?.lowercase() ?: existingUser.email,
                password = existingUser.password,
                lastModifiedDt = Clock.System.now()
            )
            userRepository.save(updatedUser)

            return UserProfileDTO(
                email = updatedUser.email,
                name = updatedUser.name,
                createdAtDt = updatedUser.createdAtDt
            )
        }
    }

    /**
     * User deletes their account.
     */
    fun delete(userId: Int): Boolean {
        val existingUser = userRepository.findById(userId) ?: throw UserNotFoundException()
        return userRepository.deleteById(existingUser.id)
    }


    /**
     * Login an existing user.
     */
    fun login(dto: UserLoginDTO): UserProfileDTO {
        val loggedInUser = userRepository.findByEmail(dto.email) ?: throw UserNotFoundException()
        val userIsAuthenticated = Authentication().validate(loggedInUser.password, dto.password.toCharArray())
        if (userIsAuthenticated) {
            LOGGER.info("[service login] User is authenticated: ${loggedInUser.id}")
            return UserProfileDTO(
                name = loggedInUser.name,
                email = loggedInUser.email,
                createdAtDt = loggedInUser.createdAtDt
            )
        } else {
            LOGGER.warn("[login] User is not authenticated: ${dto.email}")
            throw UserNotAuthenticated()
        }
    }

    /**
     * Reads data from an existing account.
     */
    fun read(dto: UserReadDTO): UserProfileDTO {
        val readUser = userRepository.findById(dto.id) ?: throw UserNotFoundException()
        LOGGER.info("[read] User Profile returned for: ${readUser.id}")
        return UserProfileDTO(
            name = readUser.name,
            email = readUser.email,
            createdAtDt = readUser.createdAtDt
        )
    }
}