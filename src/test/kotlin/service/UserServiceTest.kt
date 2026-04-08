package service

import app.sotchi.domain.exception.EmailAlreadyInUseException
import app.sotchi.domain.exception.UserNotAuthenticated
import app.sotchi.domain.exception.UserNotFoundException
import app.sotchi.domain.generic.UserRole
import app.sotchi.dto.user.*
import app.sotchi.persistence.UserActivationTable
import app.sotchi.persistence.UserRoleTable
import app.sotchi.persistence.UserTable
import app.sotchi.repository.UserRepository
import app.sotchi.repository.UserRepositoryDbImpl
import app.sotchi.service.UserService
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.*

/**
 * Tests the logic of user service.
 */
class UserServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userService: UserService

    /**
     * Setup which runs before each of the other tests. Inits an in-memory mock repository.
     */
    @BeforeTest
    fun setup() {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = "",
            databaseConfig = DatabaseConfig {
                defaultMaxAttempts = 3
            })

        transaction {
            SchemaUtils.drop(UserRoleTable, UserActivationTable, UserTable)

            SchemaUtils.create(UserTable, UserRoleTable, UserActivationTable)
        }
        userRepository = UserRepositoryDbImpl()//UserRepositoryInMemoryImpl()
        userService = UserService(userRepository)
        userService.create(
            UserCreateDTO(
                name = "test12345", email = "email@test.com", password = "test12345"
            )
        )
    }

    /**
     * Validates that the user is created based on the DTO.
     */
    @Test
    fun `create() creates user`() {
        val userIsCreated = this.userService.create(
            UserCreateDTO("Paul", "paul@test.com", "test12345")
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
        val createDTO = UserCreateDTO("Paul", "paul@test.com", "test12345")
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
            password = "newPassword123"
        )
        assertTrue(userService.update(1, updatedUser))
        assertFailsWith<UserNotAuthenticated> {
            userService.login(UserLoginDTO(email = "email@test.com", password = "test12345"))
        }
        // If login works as intended we get the AuthenticationDTO back
        val authenticatedUser = userService.login(UserLoginDTO(email = "email@test.com", password = "newPassword123"))
        assertNotNull(authenticatedUser)
        assertEquals(1, authenticatedUser.id)
    }

    /**
     * validates that the user updates their email
     */
    @Test
    fun `update() updates user email`() {
        // Login with old account works
        assertNotNull(userService.login(UserLoginDTO(email = "email@test.com", password = "test12345")))

        // After that we update the Email
        val updatedUser = UserUpdateDTO(
            name = "test1", email = "email_after_update@test.com"
        )
        assertTrue(userService.update(1, updatedUser))
        // If login works as intended we get the AuthenticationDTO back
        val authenticatedUser =
            userService.login(UserLoginDTO(email = "email_after_update@test.com", password = "test12345"))
        assertNotNull(authenticatedUser)
        assertEquals(1, authenticatedUser.id)

        // Login with old email doesn't work anymore
        assertFailsWith<UserNotFoundException> {
            userService.login(UserLoginDTO(email = "email@test.com", password = "test12345"))
        }
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
        val result = this.userService.login(UserLoginDTO(email = "email@test.com", password = "test12345"))
        assertEquals(1, result.id)
        assertEquals(UserRole.STANDARD, result.role)
    }

    /**
     * Validates that login fails for a user which does not exist
     */
    @Test
    fun `login() fails if email is not found`() {
        assertFailsWith<UserNotFoundException> {
            this.userService.login(UserLoginDTO(email = "test12345", password = "test12345"))
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
            name = "test12345",
            email = "email@test.com",
            createdAtDt = result.createdAtDt,
            activated = false,
            activatedAtDt = null
        )
        assertEquals(expected, result)
    }

    /**
     * Validates that creating a new user sets UserRole to Default Standard
     */
    @Test
    fun `create() sets default user role to standard`() {
        val userIsCreated = userService.create(
            UserCreateDTO(
                name = "test12345", email = "test2@example.com", password = "test12345"
            )
        )

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
        val loggedInUser = userService.login(
            UserLoginDTO(
                email = "email@test.com", password = "test12345"
            )
        )
        assertTrue(loggedInUser.role == UserRole.STANDARD)
    }

    /**
     * Validates that a fresh created user is not yet activated.
     */
    @Test
    fun `fresh account is not activated yet`() {
        val existingUser = userService.read(UserReadDTO(id = 1))
        assertFalse(existingUser.activated)
    }

    /**
     * Validate that a user activation returns an activated user profile.
     */
    @Test
    fun `updated account is activated`() {
        val updateUser = UserUpdateDTO(
            activated = true
        )
        val updatedUser = userService.update(
            userId = 1, dto = updateUser
        )
        assertTrue(updatedUser)
        assertTrue(userService.read(UserReadDTO(id = 1)).activated)
    }

    /**
     * Validate the following process:
     * -> User is created -> activation status is false -> activation timestamp is null
     * -> User gets updated --> activation status is true -> activation timestamp is set
     * -> User gets updated again with some other data -> activation status remains true -> activation status remains the very first one (no overwriting happening)
     */
    @Test
    fun `activation timestamps are correctly calculated`() {
        // User is created -> we expect the activation status to be false && activation timestamp to be null
        val newCreatedUser = userService.create(
            UserCreateDTO(
                name = "test123", email = "test55@example.com", password = "test12345"
            )
        )
        assertTrue(newCreatedUser)
        assertFalse(userService.read(UserReadDTO(id = 2)).activated)
        assertNull(userService.read(UserReadDTO(id = 2)).activatedAtDt)

        // User gets updated -> we expect the status to go to true -> and the timestamp to be set
        val updatedUser = UserUpdateDTO(
            activated = true
        )
        val userIsUpdated = userService.update(userId = 2, dto = updatedUser)
        val firstTimeActivatedAtDt = userService.read(UserReadDTO(id = 2)).activatedAtDt
        assertTrue(userIsUpdated)
        assertTrue(userService.read(UserReadDTO(id = 2)).activated)
        assertNotNull(firstTimeActivatedAtDt)

        // User gets updated again with some other data -> activation remains true -> activation timestamp stays the same
        val anotherUpdatedUser = UserUpdateDTO(
            name = "PabloDiEscobar"
        )
        assertTrue(userService.update(userId = 2, anotherUpdatedUser))
        assertEquals("PabloDiEscobar", userService.read(UserReadDTO(id = 2)).name)
        assertEquals(firstTimeActivatedAtDt, userService.read(UserReadDTO(id = 2)).activatedAtDt)

        // Now we also test that - in whatever case the user re-activates their account - we still keep the first time they activated
        val thirdUpdatedUser = UserUpdateDTO(
            activated = true
        )
        assertTrue(userService.update(userId = 2, thirdUpdatedUser))
        assertEquals("PabloDiEscobar", userService.read(UserReadDTO(id = 2)).name)
        assertEquals(firstTimeActivatedAtDt, userService.read(UserReadDTO(id = 2)).activatedAtDt)
    }
}