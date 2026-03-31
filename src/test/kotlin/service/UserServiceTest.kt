package service

import app.sotchi.domain.exception.EmailAlreadyInUseException
import app.sotchi.domain.exception.UserNotFoundException
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserLoginDTO
import app.sotchi.dto.user.UserProfileDTO
import app.sotchi.dto.user.UserReadDTO
import app.sotchi.dto.user.UserUpdateDTO
import app.sotchi.repository.UserRepository
import app.sotchi.repository.UserRepositoryDbImpl
import app.sotchi.repository.UserTable
import app.sotchi.service.UserService
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

/**
 * Tests the logic of user service.
 */
class UserServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userService: UserService

    /**
     * Setup which runs before each of the other tests. Inits an in-memory mock repository.
     */
    @BeforeEach
    fun setup() {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = "",
            databaseConfig = DatabaseConfig {
                defaultMaxAttempts = 3
            }
        )

        transaction {
            SchemaUtils.drop(UserTable)
            SchemaUtils.create(UserTable)
        }
        userRepository = UserRepositoryDbImpl()//UserRepositoryInMemoryImpl()
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
     * validates that re-creating a user throws an exception.
     */
    @Test
    fun `create() throws EmailAlreadyInUseException if user already exists`() {
        val createDTO = UserCreateDTO("Paul", "paul@test.com")
        this.userService.create(createDTO)
        assertFailsWith<EmailAlreadyInUseException> {
            this.userService.create(createDTO)
        }
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

    /**
     * validates that the user is correctly deleted.
     */
    @Test
    fun `delete() deletes user`() {
        val result = this.userService.delete(userId = 1)
        assertTrue(result)
    }

    /**
     * Validates that an existing user entity is returned after successful login.
     */
    @Test
    fun `login() for successful user returns UserProfileDTO`() {
        val result = this.userService.login(UserLoginDTO(email = "email@test.com"))
        assertEquals("test", result.name)
        assertEquals("email@test.com", result.email)
        assertNotNull(result.createdAtDt)
    }

    /**
     * Validates that login fails for a user which does not exist
     */
    @Test
    fun `login() fails if email is not found`() {
        assertFailsWith<UserNotFoundException> {
            this.userService.login(UserLoginDTO(email = "test"))
        }
    }

    /**
     * validates that reading a user returns the whole UserEntity.
     */
    @Test
    fun `read() returns full UserProfileDTO`() {
        val result = userService.read(UserReadDTO(id = 1))

        val expected = UserProfileDTO(
            name = "test",
            email = "email@test.com",
            createdAtDt = result.createdAtDt
        )
        assertEquals(expected, result)
    }
}