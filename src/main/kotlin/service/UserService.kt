package app.sotchi.service

import app.sotchi.domain.user.UserEntity
import app.sotchi.repository.UserRepository

class UserService(
    private val userRepository: UserRepository
) {
    /**
     * Registers a new user.
     */
    fun register(user: UserEntity): UserEntity {
        return userRepository.save(user)
    }

    /**
     * Logins an existing user.
     */
    fun login(email: String, password: String): Boolean {
        val user = userRepository.findByEmail(email)
        return user != null
    }

    /**
     * Deletes an existing user.
     */
    fun delete(id: Int): Boolean {
        val user = userRepository.findById(id)
        if (user != null) {
            userRepository.deleteById(id)
            return true
        } else {
            return false
        }
    }
}