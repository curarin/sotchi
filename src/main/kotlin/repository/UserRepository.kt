package app.sotchi.repository

import app.sotchi.domain.user.UserEntity

interface UserRepository {
    fun findById(id: Int): UserEntity?
    fun findByEmail(email: String): UserEntity?
    fun save(user: UserEntity): UserEntity
    fun deleteById(id: Int): Boolean
}