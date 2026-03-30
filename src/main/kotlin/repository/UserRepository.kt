package app.sotchi.repository

import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserLoginDTO
import app.sotchi.dto.user.UserProfileDTO
import app.sotchi.dto.user.UserReadDTO
import app.sotchi.dto.user.UserUpdateDTO

interface UserRepository {
    fun findById(id: Int): UserEntity
    fun findByEmail(email: String): UserEntity
    fun create(dto: UserCreateDTO): UserProfileDTO
    fun login(dto: UserLoginDTO): UserProfileDTO
    fun read(dto: UserReadDTO): UserProfileDTO
    fun update(id: Int, dto: UserUpdateDTO): UserProfileDTO
    fun delete(id: Int): Boolean
}