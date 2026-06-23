package app.sotchi.repository

import app.sotchi.domain.generic.UserRole
import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserUpdateDTO
import app.sotchi.messaging.DefaultEventPublisher
import app.sotchi.messaging.EventPublisher
import app.sotchi.persistence.UserActivationTable
import app.sotchi.persistence.UserRoleTable
import app.sotchi.persistence.UserTable
import app.sotchi.security.Encryption
import app.sotchi.service.UserService
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.*
import kotlin.time.Clock

/**
 * Tests the functionality of user repository
 */
class UserRepositoryImplTest {
    private lateinit var userRepository: UserRepository
    private lateinit var eventPublisher: EventPublisher
    private lateinit var userService: UserService

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
        eventPublisher = DefaultEventPublisher()
        userService = UserService(userRepository, eventPublisher)
        userService.create(
            UserCreateDTO(
                name = "test12345", email = "email@test.com", password = "test12345"
            )
        )
    }

    @Test
    fun `findById() finds user by id`() {
        assertNotNull(userRepository.findById(1))
    }

    @Test
    fun `findByEmail() finds user by email`() {
        assertNotNull(userRepository.findByEmail("email@test.com"))
    }

    @Test
    fun `save() updates existing user`() {
        val existingUser = userRepository.findById(1)
        val dto = UserUpdateDTO(
            name = "Updated User Name"
        )
        val activationStatusWillBeUpdated = dto.isActivated ?: existingUser!!.isActivated

        val updatedUser = existingUser!!.copy(
            name = dto.name ?: existingUser.name,
            email = dto.email?.trim()?.lowercase() ?: existingUser.email,
            password = existingUser.password,
            isActivated = dto.isActivated ?: existingUser.isActivated,
            // Wir prüfen ob der bestehende User einen Activation Status auf false hat & ob der Activation Status updated wird (aka aus dem DTO auf true ist)
            // In diesen Fällen setzen wir den ActivationDT auf "NOW", ansonsten übernehmen wir was vorher drinnen stand (null oder der erstmals gesetzte DT)
            activatedAtDt = if (!existingUser.isActivated && activationStatusWillBeUpdated) Clock.System.now() else existingUser.activatedAtDt,
            lastModifiedDt = Clock.System.now()
        )
        userRepository.save(updatedUser)

        val updatedUserAfterUpdate = userRepository.findById(1)
        assertEquals("Updated User Name", updatedUserAfterUpdate!!.name)
    }

    @Test
    fun `save() creates new user if no user found`() {
        val existingUser = userRepository.findById(2)
        assertNull(existingUser)

        val dto = UserCreateDTO(
            name = "New User",
            email = "new_user@example.com",
            password = "new_user"
        )
        val newUser = UserEntity(
            id = 0,
            name = dto.name,
            email = dto.email.trim().lowercase(),
            password = Encryption().hashPassword(dto.password.toCharArray()),
            createdAtDt = Clock.System.now(),
            role = UserRole.STANDARD,
            lastModifiedDt = Clock.System.now(),
            activationToken = "123",
            activationTokenValidUntil = Clock.System.now().plus(5, DateTimeUnit.MINUTE)
        )
        userRepository.save(newUser)
        assertNotNull(userRepository.findById(2))
    }

    @Test
    fun `deleteById() deletes user by id`() {
        assertNotNull(userRepository.findById(1))
        userRepository.deleteById(1)
        assertNull(userRepository.findById(1))
    }
}

