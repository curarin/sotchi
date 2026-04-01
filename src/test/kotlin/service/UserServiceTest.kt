package service

import app.sotchi.domain.exception.EmailAlreadyInUseException
import app.sotchi.domain.exception.UserNotAuthenticated
import app.sotchi.domain.exception.UserNotFoundException
import app.sotchi.domain.generic.UserRole
import app.sotchi.dto.user.*
import app.sotchi.repository.UserRepository
import app.sotchi.repository.UserRepositoryDbImpl
import app.sotchi.repository.UserTable
import app.sotchi.service.UserService
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

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
            email = "email@test.com",
            password = "test"
        ))
    }

    /**
     * Validates that the user is created based on the DTO.
     */
    @Test
    fun `create() creates user`() {
        val userIsCreated = this.userService.create(
            UserCreateDTO("Paul", "paul@test.com", "test")
        )
        assertTrue(userIsCreated)
        assertEquals("Paul", userRepository.findByEmail("paul@test.com")?.name)
        assertEquals("paul@test.com", userRepository.findByEmail("paul@test.com")?.email)
    }

    /**
     * validates that re-creating a user throws an exception.
     */
    @Test
    fun `create() throws EmailAlreadyInUseException if user already exists`() {
        val createDTO = UserCreateDTO("Paul", "paul@test.com", "test")
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
        val userIsUpdated = this.userService.update(
            userId = 1, // kommt aus dem JWT
            dto = updatedUser
        )
        assertTrue(userIsUpdated)
        assertEquals("test2", userRepository.findById(1)?.name)
        assertEquals("email@test.com", userRepository.findById(1)?.email)
    }

    /**
     * Validates that a user updating their password works as intended.
     */
    @Test
    fun `update() updates user password`() {
        val updatedUser = UserUpdateDTO(
            name = "test1",
            password = "newPassword123"
        )
        userService.update(1, updatedUser)
        assertFailsWith<UserNotAuthenticated> {
            userService.login(UserLoginDTO(email = "email@test.com", password = "test"))
        }
        // If login works as intended we get the AuthenticationDTO back
        val authenticatedUser = userService.login(UserLoginDTO(email = "email@test.com", password = "newPassword123"))
        assertNotNull(authenticatedUser)
        assertEquals(1, authenticatedUser.id)
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
    fun `login() for successful user returns correct ID`() {
        val result = this.userService.login(UserLoginDTO(email = "email@test.com", password = "test"))
        assertEquals(1, result.id)
        assertEquals(UserRole.STANDARD, result.role)
    }

    /**
     * Validates that login fails for a user which does not exist
     */
    @Test
    fun `login() fails if email is not found`() {
        assertFailsWith<UserNotFoundException> {
            this.userService.login(UserLoginDTO(email = "test", password = "test"))
        }
    }

    /**
     * Validates that login fails for a user with a wrong password
     */
    @Test
    fun `login() fails if password is invalid`() {
        assertFailsWith<UserNotAuthenticated> {
            this.userService.login(UserLoginDTO(email = "email@test.com", password = "invalid"))
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

    /**
     * Validates that creating a new user sets UserRole to Default Standard
     */
    @Test
    fun `create() sets default user role to standard`() {
        val userIsCreated = userService.create(UserCreateDTO(
            name = "test",
            email = "test2@example.com",
            password = "test"
        ))

        val createdUser = userRepository.findByEmail("test2@example.com")
        assertTrue(userIsCreated)
        assertEquals(UserRole.STANDARD, createdUser?.role)
    }

    /**
     * Validates that UserAuthenticationDTO returns correct user role.
     * In future we implement an 'upgrade plan' method in UserService
     * which sets
     */
    @Test
    fun `login() returns correct default user role`() {
        val loggedInUser = userService.login(UserLoginDTO(
            email = "email@test.com",
            password = "test"
        ))
        assertTrue(loggedInUser.role == UserRole.STANDARD)
    }
}