package service

import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserUpdateDTO
import app.sotchi.repository.UserRepository
import app.sotchi.repository.UserRepositoryImpl
import app.sotchi.service.UserService
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the logic of user service.
 */
class UserServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userService: UserService

    /**
     * Setup which runs before all the other tests. Inits an in-memory mock repository.
     */
    @BeforeTest
    fun setup() {
        userRepository = UserRepositoryImpl()
        userService = UserService(userRepository)
        userService.create(UserCreateDTO(
            name = "test",
            email = "email@test.com"
        ))
    }

    /**
     * Validates that the user is created based on the DTO.
     */
    @Test
    fun `create() creates user`() {
        val result = this.userService.create(
            UserCreateDTO("Paul", "paul@test.com")
        )
        assertEquals("Paul", result.name)
        assertEquals("paul@test.com", result.email)
    }

    /**
     * Validates that the user is correctly updated - while the other existing data stays as is.
     */
    @Test
    fun `update() updates user`() {
        val updatedUser = UserUpdateDTO(
            name = "test2"
        )
        val result = this.userService.update(
            userId = 1,
            dto = updatedUser
        )
        assertEquals("test2", result.name)
        assertEquals("email@test.com", result.email)
    }
}