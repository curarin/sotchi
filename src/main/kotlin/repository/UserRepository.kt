package app.sotchi.repository

import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserLoginDTO
import app.sotchi.dto.user.UserReadDTO
import app.sotchi.dto.user.UserUpdateDTO

interface UserRepository {
    fun findById(id: Int): UserEntity
    fun findByEmail(email: String): UserEntity
    fun create(dto: UserCreateDTO): UserEntity
    fun login(dto: UserLoginDTO): UserEntity
    fun read(dto: UserReadDTO): UserEntity
    fun update(id: Int, dto: UserUpdateDTO): UserEntity
    fun delete(id: Int): Boolean
}