package app.sotchi.repository

import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserLoginDTO
import app.sotchi.dto.user.UserProfileDTO
import app.sotchi.dto.user.UserReadDTO
import app.sotchi.dto.user.UserUpdateDTO

interface UserRepository {
    fun findById(id: Int): UserEntity?
    fun findByEmail(email: String): UserEntity?
    fun save(user: UserEntity): UserEntity
    fun deleteById(id: Int): Boolean
}