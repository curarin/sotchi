package app.sotchi.service

import app.sotchi.domain.exception.EmailAlreadyInUseException
import app.sotchi.domain.exception.UserNotFoundException
import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserLoginDTO
import app.sotchi.dto.user.UserReadDTO
import app.sotchi.dto.user.UserUpdateDTO
import app.sotchi.repository.UserRepository

class UserService(
    private val userRepository: UserRepository
) {

    /**
     * User creates a new account.
     */
    fun create(dto: UserCreateDTO): UserEntity {
        // ToDo: Bessere Implementierung als diese Yolo Catch der Exception - die Abhängigkeit will ich eig nicht.
        try {
            val existingUser = userRepository.findByEmail(dto.email)
            throw EmailAlreadyInUseException()
        } catch (exception: UserNotFoundException) {
            return userRepository.create(dto)
        }
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
     * Login an existing user.
     */
    fun login(dto: UserLoginDTO): UserEntity {
        return userRepository.findByEmail(dto.email)
    }

    /**
     * Reads data from an existing account.
     */
    fun read(dto: UserReadDTO): UserEntity {
        return userRepository.read(dto)
    }
}